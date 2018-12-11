package co.inventorsoft.scripty.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import co.inventorsoft.scripty.exception.ApplicationException;
import co.inventorsoft.scripty.model.dto.ProjectDto;
import co.inventorsoft.scripty.model.entity.Project;
import co.inventorsoft.scripty.model.entity.User;
import co.inventorsoft.scripty.repository.ProjectRepository;
import co.inventorsoft.scripty.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author lzabidovsky 
 */
@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectService {

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	UserRepository userRepository;

	final static String DEFAULT_DESCRIPTION = "";
	final static boolean DEFAULT_VISIBILITY = true;

	@Value("${path.local.repo}")
	String pathLocalRepo;
	@Value("${directory.separator}")
	String directorySeparator;

	public long saveProject(ProjectDto project, String username) {
		User user = userRepository.findByEmail(username).orElseThrow(() -> new ApplicationException("There is no " + username, HttpStatus.NOT_FOUND));
		if (projectRepository.findByNameAndUser(project.getName(), user).isPresent()) {
			throw new ApplicationException("Project with name " + project.getName() + " already exist for " + username, HttpStatus.CONFLICT);
		}

		Project newProject = new Project();
		newProject.setName(project.getName());
		if(project.getDescription() != null) {
			newProject.setDescription(project.getDescription());
		} else {
			newProject.setDescription(DEFAULT_DESCRIPTION);
		}
		if(project.getVisibility() != null) {
			newProject.setVisibility(project.getVisibility());
		} else {
			newProject.setVisibility(DEFAULT_VISIBILITY);
		}
		newProject.setArchive(false);
		
		String projectPath = pathLocalRepo + username + directorySeparator + project.getName();
		createProjectPath(projectPath);
		newProject.setPath(projectPath);
		newProject.setUser(user);
		newProject.setCreateDate(LocalDateTime.now());
		
		return projectRepository.save(newProject).getId();
	}

	private void createProjectPath(String projectPath) {
		Path path = Paths.get(projectPath);
		if(!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				throw new ApplicationException("Error repo creating " + projectPath, HttpStatus.FAILED_DEPENDENCY);
			}
		}
	}

}
