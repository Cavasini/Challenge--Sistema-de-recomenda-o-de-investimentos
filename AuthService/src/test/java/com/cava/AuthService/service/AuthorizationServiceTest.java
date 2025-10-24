//package com.cava.AuthService.service;
//
//// Importe sua classe de modelo 'User'
//import com.cava.AuthService.model.User;
//import com.cava.AuthService.repository.UserRepository;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.userdetails.UserDetails; // Ainda usamos a interface
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthorizationServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private AuthorizationService authorizationService;
//
//    private String existingEmail;
//    private String nonExistingEmail;
//
//    // AQUI ESTÁ A MUDANÇA:
//    // Trocamos o mock da interface 'UserDetails' pelo mock da sua classe 'User'
//    private User mockUser;
//
//    @BeforeEach
//    void setUp() {
//        existingEmail = "user@example.com";
//        nonExistingEmail = "notfound@example.com";
//
//        // AQUI ESTÁ A MUDANÇA:
//        // Agora mockamos a sua classe de modelo concreta.
//        mockUser = mock(User.class);
//    }
//
//    @Test
//    @DisplayName("Deve retornar UserDetails quando o usuário é encontrado pelo email")
//    void testLoadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
//        // 1. Arrange (Configuração)
//
//        // Configuramos o comportamento do nosso mockUser
//        // (Assumindo que seu método getUsername() retorna o email,
//        // já que é o padrão do UserDetails)
//        when(mockUser.getUsername()).thenReturn(existingEmail);
//
//        // AQUI ESTÁ A MUDANÇA:
//        // Dizemos ao repositório para retornar o 'mockUser' (que é do tipo User)
//        when(userRepository.findByEmail(existingEmail))
//                .thenReturn(mockUser);
//
//        // 2. Act (Ação)
//        // O método 'loadUserByUsername' retorna 'UserDetails'
//        // Isso funciona, pois a classe 'User' IMPLEMENTA 'UserDetails'
//        UserDetails result = authorizationService.loadUserByUsername(existingEmail);
//
//        // 3. Assert (Verificação)
//        assertNotNull(result, "O UserDetails não deveria ser nulo");
//
//        // Verificamos se o username (email) está correto
//        assertEquals(existingEmail, result.getUsername(), "O email do usuário retornado está incorreto");
//
//        // Verificamos se o objeto retornado é o MESMO mock que criamos
//        assertEquals(mockUser, result, "O objeto UserDetails retornado não é o mockUser esperado");
//
//        // Verifica as chamadas dos mocks
//        verify(userRepository, times(1)).findByEmail(existingEmail);
//        verify(mockUser, times(1)).getUsername();
//    }
//
//    @Test
//    @DisplayName("Deve lançar UsernameNotFoundException quando o usuário não é encontrado")
//    void testLoadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
//        // 1. Arrange
//        // Este teste não muda, pois o repositório simplesmente retorna null.
//        when(userRepository.findByEmail(nonExistingEmail))
//                .thenReturn(null);
//
//        // 2. Act & 3. Assert
//        UsernameNotFoundException exception = assertThrows(
//                UsernameNotFoundException.class,
//                () -> {
//                    authorizationService.loadUserByUsername(nonExistingEmail);
//                },
//                "A exceção UsernameNotFoundException não foi lançada"
//        );
//
//        String expectedMessage = "Usuário não encontrado com o email: " + nonExistingEmail;
//        assertEquals(expectedMessage, exception.getMessage(), "A mensagem da exceção está incorreta");
//
//        // Verifica a chamada
//        verify(userRepository, times(1)).findByEmail(nonExistingEmail);
//
//        // Garante que nenhuma interação ocorreu com o mockUser neste cenário
//        verifyNoInteractions(mockUser);
//    }
//}