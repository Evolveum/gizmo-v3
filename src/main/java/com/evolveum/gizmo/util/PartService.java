package com.evolveum.gizmo.util;

import com.evolveum.gizmo.data.Part;
import com.evolveum.gizmo.repository.PartRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartService {
    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    //@PostConstruct
    //public void assignColorsIfMissing() {
        //List<Part> parts = partRepository.findAllWithoutColor();
        //for (Part part : parts) {
            //part.setColor(ColorUtils.getRandomFromPalette());
           // partRepository.save(part);
        //}
    //}
}

