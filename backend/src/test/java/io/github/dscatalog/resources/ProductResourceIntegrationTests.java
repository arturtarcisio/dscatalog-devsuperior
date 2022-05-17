package io.github.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ProductResourceIntegrationTests {
	
	private final MockMvc mockMvc;

	@Autowired
	public ProductResourceIntegrationTests(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
	
	private long existingId;
	private long nonExistentId;
	private Long countTotalProduct;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistentId = 900L;
		countTotalProduct = 25L;
	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		var resultActions = mockMvc
				.perform(get("/products?page=0&size=12&sort=name,asc")
						.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.content").exists());
		resultActions.andExpect(jsonPath("$.totalElements").value(countTotalProduct));
		resultActions.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		resultActions.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		resultActions.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}
	
	

}
