package co.inventorsoft.scripty.service;
import co.inventorsoft.scripty.model.dto.EmailDto;
import co.inventorsoft.scripty.model.dto.PictureDto;
import co.inventorsoft.scripty.model.dto.UserDto;
import co.inventorsoft.scripty.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void registerNewUserAccount(UserDto userDto);
    void resendRegistrationToken(EmailDto emailDto);
    void validateVerificationToken(String token);
    User findByEmail(String email);
    void sendResetPasswordToken(EmailDto emailDto);
    void setPicture(String email, MultipartFile picture);
    PictureDto getPicture(Long id);
}
