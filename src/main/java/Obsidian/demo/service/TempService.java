package Obsidian.demo.service;

import Obsidian.demo.Repository.TempRepository;
import Obsidian.demo.domain.common.TempEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import java.io.File;
import java.nio.file.StandardOpenOption;

@Service
@RequiredArgsConstructor
public class TempService {
    private final TempRepository tempRepository;
    private final String basePath = "/Users/iseoyeon/Desktop/Obsidian-FISA-4th";

    public List<TempEntity> getAll() {
        return tempRepository.findAll();
    }

    public Optional<TempEntity> getById(Long id) {
        return tempRepository.findById(id);
    }

    @Transactional
    public TempEntity create(TempEntity tempEntity) {
        if (tempEntity.getParent() != null) {
            TempEntity parent = tempRepository.findById(tempEntity.getParent().getId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            tempEntity.setParent(parent);
        }
        TempEntity savedEntity = tempRepository.save(tempEntity);
        saveToLocal(savedEntity);
        return savedEntity;
    }

    @Transactional
    public TempEntity update(Long id, TempEntity tempEntity) {
        return tempRepository.findById(id)
                .map(existing -> {
                    existing.setName(tempEntity.getName());
                    existing.setType(tempEntity.getType());
                    if (tempEntity.getParent() != null) {
                        TempEntity parent = tempRepository.findById(tempEntity.getParent().getId())
                                .orElseThrow(() -> new RuntimeException("Parent not found"));
                        existing.setParent(parent);
                    }
                    TempEntity updatedEntity = tempRepository.save(existing);
                    saveToLocal(updatedEntity);
                    return updatedEntity;
                }).orElseThrow(() -> new RuntimeException("Entity not found"));
    }

    @Transactional
    public void delete(Long id) {
        tempRepository.findById(id).ifPresent(entity -> {
            deleteFromLocal(entity);
            tempRepository.deleteById(id);
        });
    }

    private void saveToLocal(TempEntity entity) {
        try {
            File directory;
            if (entity.getParent() != null) {
                directory = new File(basePath, entity.getParent().getName());
            } else {
                directory = new File(basePath);
            }

            if (entity.getType().equals("FOLDER")) {
                File folder = new File(directory, entity.getName());
                folder.mkdirs();
            } else if (entity.getType().equals("FILE")) {
                File file = new File(directory, entity.getName() + ".json");
                Files.writeString(file.toPath(), "{\"id\": " + entity.getId() + ", \"name\": \"" + entity.getName() + "\"}\n", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFromLocal(TempEntity entity) {
        File target;
        if (entity.getParent() != null) {
            target = new File(basePath, entity.getParent().getName() + "/" + entity.getName());
        } else {
            target = new File(basePath, entity.getName());
        }

        if (target.exists()) {
            if (target.isDirectory()) {
                for (File file : target.listFiles()) {
                    file.delete();
                }
            }
            target.delete();
        }
    }
}