//    package com.cava.AuthService.controller;
//
//    import com.cava.AuthService.model.AuthenticationDto;
//    import com.cava.AuthService.model.RegisterRequestDTO;
//    import com.cava.AuthService.model.User;
//    import com.cava.AuthService.repository.UserRepository;
//    import com.fasterxml.jackson.databind.ObjectMapper;
//    import org.junit.jupiter.api.BeforeEach;
//    import org.junit.jupiter.api.DisplayName;
//    import org.junit.jupiter.api.Test;
//    import org.springframework.beans.factory.annotation.Autowired;
//    import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//    import org.springframework.boot.test.context.SpringBootTest;
//    import org.springframework.http.MediaType;
//    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//    import org.springframework.test.context.ActiveProfiles;
//    import org.springframework.test.web.servlet.MockMvc;
//    import org.springframework.transaction.annotation.Transactional;
//
//    // Importações estáticas para facilitar a leitura dos testes
//    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//    import static org.hamcrest.Matchers.is;
//    import static org.hamcrest.Matchers.notNullValue;
//    import static org.junit.jupiter.api.Assertions.assertTrue;
//
//    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//    @AutoConfigureMockMvc
//    @ActiveProfiles("test")
//    @Transactional
//    public class AuthenticationControllerIntegrationTest {
//
//        @Autowired
//        private MockMvc mockMvc;
//
//        @Autowired
//        private UserRepository userRepository;
//
//        @Autowired
//        private ObjectMapper objectMapper;
//
//        private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//        @BeforeEach
//        void setup() {
//            userRepository.deleteAll();
//        }
//
//        @Test
//        @DisplayName("Deve registrar um novo usuário com sucesso e retornar 201 Created")
//        void register_WithValidData_ShouldReturnCreated() throws Exception {
//            var registerDTO = new RegisterRequestDTO("Test User", "test@example.com", "password123");
//
//            mockMvc.perform(post("/auth/register")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(registerDTO)))
//                    .andExpect(status().isCreated())
//                    .andExpect(jsonPath("$.username", is("Test User")))
//                    .andExpect(jsonPath("$.email", is("test@example.com")))
//                    .andExpect(jsonPath("$.id", notNullValue()));
//
//            User savedUser = (User) userRepository.findByEmail("test@example.com");
//            assertTrue(savedUser != null);
//            assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
//        }
//
//        @Test
//        @DisplayName("Não deve registrar um usuário com email duplicado e retornar 400 Bad Request")
//        void register_WithDuplicateEmail_ShouldReturnBadRequest() throws Exception {
//            User existingUser = new User("Existing User", "duplicate@example.com", passwordEncoder.encode("pass"));
//            userRepository.save(existingUser);
//
//            var registerDTO = new RegisterRequestDTO("New User", "duplicate@example.com", "password123");
//
//            // Act & Assert
//            mockMvc.perform(post("/auth/register")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(registerDTO)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$", is("Username or Email already used!")));
//        }
//
//        @Test
//        @DisplayName("Não deve registrar com dados inválidos (ex: email mal formatado) e retornar 400 Bad Request")
//        void register_WithInvalidData_ShouldReturnBadRequest() throws Exception {
//
//            var registerDTO = new RegisterRequestDTO("Test User", "not-an-email", "password123");
//
//            mockMvc.perform(post("/auth/register")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(registerDTO)))
//                    .andExpect(status().isBadRequest()); // O @Valid deve retornar 400
//        }
//
//    //    @Test
//    //    @DisplayName("Deve logar com credenciais válidas e retornar 200 OK com token")
//    //    void login_WithValidCredentials_ShouldReturnOkAndToken() throws Exception {
//    //        // --- Arrange ---
//    //        // Preparamos o banco de dados com os dados exatos do seu exemplo
//    //        String rawPassword = "teste";
//    //        String email = "matheus@gmail.com";
//    //        String username = "math";
//    //
//    //        // Criptografa a senha como o /register faria
//    //        String encryptedPassword = new BCryptPasswordEncoder().encode(rawPassword);
//    //        User user = new User(username, email, encryptedPassword);
//    //        userRepository.save(user); // Salva o usuário no H2
//    //
//    //        // O DTO de request (Body)
//    //        var loginDTO = new AuthenticationDto(email, rawPassword);
//    //
//    //        // --- Act & Assert ---
//    //        mockMvc.perform(post("/auth/login")
//    //                        .contentType(MediaType.APPLICATION_JSON)
//    //                        .content(objectMapper.writeValueAsString(loginDTO)))
//    //                .andExpect(status().isOk()) // Espera HTTP 200
//    //
//    //                // Valida o objeto "user" aninhado
//    //                .andExpect(jsonPath("$.user.id", is(user.getId().toString()))) // Compara com o ID salvo
//    //                .andExpect(jsonPath("$.user.username", is(username)))
//    //                .andExpect(jsonPath("$.user.email", is(email)))
//    //
//    //                // Valida o objeto "auth" aninhado
//    //                .andExpect(jsonPath("$.auth.accessToken", notNullValue())) // O token é dinâmico, só checamos se existe
//    //                .andExpect(jsonPath("$.auth.tokenType", is("Bearer")))
//    //                .andExpect(jsonPath("$.auth.expiresIn", is(7200)));
//    //    }
//
//        @Test
//        @DisplayName("Não deve logar com senha inválida e retornar 404 Not Found")
//        void login_WithInvalidPassword_ShouldReturnNotFound() throws Exception {
//            User user = new User("Test User", "login@example.com", passwordEncoder.encode("correct_password"));
//            userRepository.save(user);
//
//            var loginDTO = new AuthenticationDto("login@example.com", "wrong_password");
//
//            mockMvc.perform(post("/auth/login")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(loginDTO)))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$", is("Usuário não registrado ou senha inválida")));
//        }
//
//        @Test
//        @DisplayName("Não deve logar com usuário inexistente e retornar 404 Not Found")
//        void login_WithNonExistentUser_ShouldReturnNotFound() throws Exception {
//            var loginDTO = new AuthenticationDto("nosuchuser@example.com", "password");
//
//            mockMvc.perform(post("/auth/login")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(loginDTO)))
//                    .andExpect(status().isNotFound()) // Espera HTTP 404
//                    .andExpect(jsonPath("$", is("Usuário não registrado ou senha inválida")));
//        }
//
//    }
