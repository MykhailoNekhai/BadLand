package ua.uni.achivments.repo;

import ua.uni.base_repo.PRepository;
import ua.uni.achivments.domain.Achievements;
import ua.uni.objects.ID;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AchievementsRepository extends PRepository<Achievements> {
    private final Map<String, ID> codeToId = new HashMap<>();

    @Override
    protected ID getId(Achievements object) {
        return object.getId();
    }

    @Override
    public void add(Achievements object) {
        validateCode(object.getCode());
        if (codeToId.containsKey(object.getCode())) {
            throw new IllegalArgumentException("Achievement with this code already exists");
        }
        super.add(object);
        codeToId.put(object.getCode(), object.getId());
    }

    @Override
    public void update(Achievements object) {
        validateCode(object.getCode());
        Optional<Achievements> existingById = findById(object.getId());
        if (existingById.isEmpty()) {
            throw new IllegalArgumentException("No achievement with this id");
        }

        ID existingCodeOwner = codeToId.get(object.getCode());
        if (existingCodeOwner != null && !existingCodeOwner.equals(object.getId())) {
            throw new IllegalArgumentException("Achievement with this code already exists");
        }

        String oldCode = existingById.get().getCode();
        super.update(object);
        if (!oldCode.equals(object.getCode())) {
            codeToId.remove(oldCode);
        }
        codeToId.put(object.getCode(), object.getId());
    }

    @Override
    public void delete(Achievements object) {
        super.delete(object);
        codeToId.remove(object.getCode());
    }

    @Override
    public void replaceAll(java.util.List<Achievements> items) {
        codeToId.clear();
        for (Achievements item : items) {
            validateCode(item.getCode());
            if (codeToId.containsKey(item.getCode())) {
                throw new IllegalArgumentException("Duplicate code in replaceAll: " + item.getCode());
            }
            codeToId.put(item.getCode(), item.getId());
        }
        super.replaceAll(items);
    }

    public Optional<Achievements> findByCode(String code) {
        validateCode(code);
        ID id = codeToId.get(code);
        if (id == null) {
            return Optional.empty();
        }
        return findById(id);
    }

    private void validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Achievement code must not be blank");
        }
    }
}
