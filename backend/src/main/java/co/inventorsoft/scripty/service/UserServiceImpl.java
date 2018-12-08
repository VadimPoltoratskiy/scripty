package co.inventorsoft.scripty.service;
import co.inventorsoft.scripty.exception.ApplicationException;
import co.inventorsoft.scripty.model.dto.EmailDto;
import co.inventorsoft.scripty.model.dto.ImageTypes;
import co.inventorsoft.scripty.model.dto.PictureDto;
import co.inventorsoft.scripty.model.entity.PasswordToken;
import co.inventorsoft.scripty.model.entity.Picture;
import co.inventorsoft.scripty.model.entity.User;
import co.inventorsoft.scripty.model.entity.VerificationToken;
import co.inventorsoft.scripty.model.dto.UserDto;
import co.inventorsoft.scripty.repository.PasswordTokenRepository;
import co.inventorsoft.scripty.repository.UserRepository;
import co.inventorsoft.scripty.repository.VerificationTokenRepository;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.*;
/**
 *
 * @author Symyniuk
 *
 */
@Service
@Transactional
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;
    private VerificationTokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           VerificationTokenRepository tokenRepository,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService,
                           PasswordTokenRepository passwordTokenRepository){
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.passwordTokenRepository = passwordTokenRepository;
    }

    public User findByEmail(final String email){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(!userOptional.isPresent()){
            throw new ApplicationException("Email is not found!", HttpStatus.BAD_REQUEST);
        }
        return userOptional.get();
    }

    public void registerNewUserAccount(final UserDto userDto){
        if(userRepository.findByEmail(userDto.getEmail()).isPresent()){
            throw new ApplicationException("There is an account with that email address: " +  userDto.getEmail(), HttpStatus.BAD_REQUEST);
        }
        final User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getValidPassword()));
        user.setEmail(userDto.getEmail());
        user.setEnabled(false);
        user.setRole("User");
        userRepository.save(user);
        final String token = UUID.randomUUID().toString();
        createVerificationTokenForUser(user, token);
        emailService.sendEmailWithVerificationLink(user, token);
    }

    public void resendRegistrationToken(final EmailDto emailDto){
        if(!userRepository.findByEmail(emailDto.toString()).isPresent()){
            throw new ApplicationException("Please complete the registration first!", HttpStatus.OK);
        }
        final User user = findByEmail(emailDto.toString());
        final VerificationToken verificationToken = generateNewVerificationToken(user);
        emailService.resendEmailWithVerificationLink(user, verificationToken);
    }

    public void validateVerificationToken(final String token) {
        Optional<VerificationToken> verificationTokenOptional =tokenRepository.findByToken(token);
        if(!verificationTokenOptional.isPresent()){
            throw new ApplicationException("Wrong link", HttpStatus.BAD_REQUEST);
        }
        final VerificationToken verificationToken = verificationTokenOptional.get();
        final User user = verificationToken.getUser();
        final Instant instantExp = verificationToken.getExpiryDate();
        final Instant instant = Clock.systemDefaultZone().instant();
        if ((instantExp.isBefore(instant))) {
            throw new ApplicationException("Time of user verification link has expired", HttpStatus.BAD_REQUEST);
        }
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
    }

    public void setPicture(String email, MultipartFile picture) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!ImageTypes.contains(Files.getFileExtension(picture.getOriginalFilename()))) throw new ApplicationException("Incorrect file extension", HttpStatus.BAD_REQUEST);
        if (user.isPresent()) {
            Picture userPic = new Picture();
            try {
                userPic.setContent(picture.getBytes());
                userPic.setExtension(picture.getContentType());
                user.get().setPicture(userPic);
            } catch (IOException e) {
                throw new ApplicationException("File's empty. Please, try to use another one!", HttpStatus.BAD_REQUEST);
            }
        } else
            throw new ApplicationException("User not found", HttpStatus.BAD_REQUEST);
        userRepository.save(user.get());
    }

    public PictureDto getPicture(Long id) {
        Optional<User> user = userRepository.findById(id);
        PictureDto image = new PictureDto();

        if(user.isPresent()) {
            if (!(user.get().getPicture() == null)) {
                image.setExtension((user.get().getPicture().getExtension()));
                image.setContent(Base64.getEncoder().encodeToString(user.get().getPicture().getContent()));
            } else
                throw new ApplicationException("User has no profile picture.", HttpStatus.NOT_FOUND);
        } else
            throw new ApplicationException("User not found.", HttpStatus.NOT_FOUND);
        return image;
    }

    private void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    private VerificationToken generateNewVerificationToken(final User user) {
        return tokenRepository.findByUser(user)
               .map(token -> token.updateToken(UUID.randomUUID().toString()))
               .map(tokenRepository::save)
               .orElseThrow(()-> new ApplicationException("Token not found", HttpStatus.OK));
    }

    private void createResetPasswordToken(final User user, final String passwordToken){
        final PasswordToken resetPasswordToken = new PasswordToken(passwordToken, user);
        passwordTokenRepository.save(resetPasswordToken);
    }

    public void sendResetPasswordToken(final EmailDto emailDto){
        Optional<User> userOptional = userRepository.findByEmail(emailDto.getEmail());
        if(userOptional.isPresent()) {
            final User user = userOptional.get();
            final String resetPasswordToken = UUID.randomUUID().toString();
            createResetPasswordToken(user, resetPasswordToken);
            emailService.sendEmailWithResetPasswordToken(user, resetPasswordToken);
        }
    }
}