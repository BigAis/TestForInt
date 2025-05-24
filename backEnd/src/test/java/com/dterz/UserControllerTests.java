package com.dterz;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dterz.dtos.UserDTO;
import com.dterz.model.User;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests extends BaseTests {

    @Test
    @WithMockUser("spring")
    public void testGetById() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        this.mockMvc.perform(get("/api/user/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("spring"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("spring")
    public void testGetAll() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(user);
        when(userRepository.findAll()).thenReturn(userList);
        this.mockMvc.perform(get("/api/user")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userName").value("spring"));
    }

    @Test
    @WithMockUser("spring")
    public void testUpdate() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUserName("spring");
        userDTO.setFistName("Spring");
        userDTO.setSurName("Framework");
        userDTO.setAge(25);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        this.mockMvc.perform(post("/api/user")
                        .content(asJsonString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("spring")
    public void testCreateAccount() throws Exception {
        this.mockMvc.perform(get("/api/user/_draft")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
                .andExpect(status().isOk());
    }
}