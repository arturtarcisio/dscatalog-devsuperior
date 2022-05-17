package io.github.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.dscatalog.dto.ProductDTO;
import io.github.dscatalog.services.ProductService;
import io.github.dscatalog.services.exceptions.ResourceNotFoundException;
import io.github.dscatalog.tests.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private ProductService service;
	
	private PageImpl<ProductDTO> page;
	private ProductDTO productDTO;
	private Long existentId;
	private Long nonExistentId;
	private Long idWithDataIntegrityViolation;
	
	@BeforeEach
	void setup() throws Exception {		
		productDTO = Factory.createProductDTO();
		existentId = 1L;
		nonExistentId = 1009L;
		idWithDataIntegrityViolation = 2L;
		
		
		// FindAllPaged
		page = new PageImpl<>(List.of(productDTO));		
		when(service.findAllPaged(any())).thenReturn(page);
		
		//FindById
		when(service.findById(existentId)).thenReturn(productDTO);
		when(service.findById(nonExistentId)).thenThrow(ResourceNotFoundException.class);
		
		//Update
		when(service.update(eq(nonExistentId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);
		when(service.update(eq(existentId), ArgumentMatchers.any())).thenReturn(productDTO);
		
		//Delete
		doNothing().when(service).delete(existentId);
		doThrow(EmptyResultDataAccessException.class).when(service).delete(nonExistentId);
		doThrow(DataIntegrityViolationException.class).when(service).delete(idWithDataIntegrityViolation);
		
		//Insert
		when(service.insert(ArgumentMatchers.any())).thenReturn(productDTO);
		
	}
	
	@Test
	public void findAllShouldReturnOk() throws Exception{
		ResultActions result = mockMvc.perform(get("/products")
				.accept(MediaType.APPLICATION_JSON));
				
		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExist() throws Exception{
		ResultActions result = mockMvc.perform(get("/products/{id}", existentId)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception{
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistentId);
		});
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception{
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistentId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldUpdateWhenIdExist() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", existentId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void deleteShouldNoContentWhenIdExist() throws Exception{
		ResultActions result = mockMvc.perform(delete("/products/{id}", existentId)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNoContent());		
		
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception{
		ResultActions result = mockMvc.perform(delete("/products/{id}", nonExistentId)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}	
		
	@Test
	public void createProductShouldReturnProductDTOCreated() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		var result = mockMvc.perform(post("/products")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		
	}

}
