package technikal.task.fishmarket.controllers;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.models.Photo;
import technikal.task.fishmarket.repository.FishRepository;

@Controller
@RequestMapping("/fish")
public class FishController {
	
	@Autowired
	private FishRepository repo;
	
	@GetMapping({"", "/"})
	public String showFishList(Model model) {
		List<Fish> fishlist = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("fishlist", fishlist);
		return "index";
	}

	@GetMapping("/login")
	public String loginPage(){
		return "fish/login";
	}
	
	@GetMapping("/create")
	public String showCreatePage(Model model) {
		FishDto fishDto = new FishDto();
		model.addAttribute("fishDto", fishDto);
		return "createFish";
	}
	
	@GetMapping("/delete")
	public String deleteFish(@RequestParam int id) {
		try {
			Fish fish = repo.findById(id).get();
			
//			Path imagePath = Paths.get("public/images/"+fish.getImageFileName());
//			Files.delete(imagePath);
			if (fish.getPhotos() != null) {
				for (Photo photo : fish.getPhotos()) {
					Path imagePath = Paths.get("target/classes/static/images/" + photo.getUrl().replace("/images/", ""));
					Files.deleteIfExists(imagePath);
				}
			}
			repo.delete(fish);
			
		} catch(Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
		
		return "redirect:/fish";
	}
	
	@PostMapping("/create")
	public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result,
						  @RequestParam("imageFiles") List<MultipartFile> imageFiles) {
		
//		if(fishDto.getImageFiles().isEmpty()) {
//			result.addError(new FieldError("fishDto", "imageFile", "Потрібне фото рибки"));
//		}

		if(imageFiles.isEmpty()){
			result.addError(new FieldError("fishDto", "imageFiles", "Потрібне принаймні одне фото рибки"));
		}
		
		if(result.hasErrors()) {
			return "createFish";
		}
		
//		List<MultipartFile> images = fishDto.getImageFiles();
//		Date catchDate = new Date();
//		String storageFileName = catchDate.getTime() + "_" + images.getOriginalFilename();

		Fish fish = new Fish();

		fish.setName(fishDto.getName());
		fish.setPrice(fishDto.getPrice());
		fish.setCatchDate(new Date());
//		fish.setImageFileName(storageFileName);

		String uploadDir = "target/classes/static/images/";
		try {
			Files.createDirectories(Paths.get(uploadDir));
			List<Photo> photos = new ArrayList<>();

//			Path uploadPath = Paths.get(uploadDir);
//			if(!Files.exists(uploadPath)) {
//				Files.createDirectories(uploadPath);
//			}

			for(MultipartFile file : imageFiles){
				if(!file.isEmpty()){
					String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
					Path uploadPath = Paths.get(uploadDir + fileName);
					try (InputStream inputStream = file.getInputStream()){
						Files.copy(inputStream,uploadPath,StandardCopyOption.REPLACE_EXISTING);
					}
					Photo photo = new Photo();
					photo.setUrl("/images/" + fileName);
					photo.setFish(fish);
					photos.add(photo);
				}
			}
			fish.setPhotos(photos);

//			try(InputStream inputStream = image.getInputStream()){
//				Files.copy(inputStream, Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
//			}

		}
		catch(IOException ex){
			System.out.println("Exception: " + ex.getMessage());
			result.addError(new FieldError("fishDto", "imageFiles", "Помилка при завантаженні фото: " + ex.getMessage()));
			return "createFish";
		}
		catch(Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			return "createFish";
		}

		repo.save(fish);
		return "redirect:/fish";
	}
}
