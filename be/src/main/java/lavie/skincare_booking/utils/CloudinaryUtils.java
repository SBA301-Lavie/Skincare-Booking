package lavie.skincare_booking.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
public class CloudinaryUtils {
    private final Cloudinary cloudinary;
    @Value("${cloudinary.url}")
    private String cloudinaryUrl;

    // Constructor to initialize Cloudinary with credentials from the .env file
    public CloudinaryUtils() {
        if(cloudinaryUrl == null) {
            cloudinaryUrl = System.getenv("CLOUDINARY_URL");
        }
        cloudinary = new Cloudinary(cloudinaryUrl);
    }

    // Method to upload an image to Cloudinary and return the URL
    public String uploadImage(MultipartFile file) throws IOException {
        // Upload the image file
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "use_filename", false,
                "unique_filename", false,
                "overwrite", true
        );

        // Convert MultipartFile to a format that Cloudinary accepts and upload
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

        // Get the URL from the upload result
        return (String) uploadResult.get("url");
    }
}
