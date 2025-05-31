package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.JuniorDeveloper;
import com.workintech.s17d2.model.MidDeveloper;
import com.workintech.s17d2.model.SeniorDeveloper;
import com.workintech.s17d2.tax.DeveloperTax;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    private final Taxable taxable;
    public Map<Integer, Developer> developers;

    @Autowired
    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    public Taxable getTaxable() {
        return taxable;
    }

    public Map<Integer, Developer> getDevelopers() {
        return developers;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAll() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        Developer dev = developers.get(id);
        if (dev != null) {
            return ResponseEntity.ok(dev);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Developer not found");
    }

    @PostMapping
    public ResponseEntity<Developer> create(@RequestBody Developer developer) {
        double salary = developer.getSalary();

        switch (developer.getExperience()) {
            case JUNIOR -> {
                salary -= salary * taxable.getSimpleTaxRate() / 100;
                developer = new JuniorDeveloper(developer.getId(), developer.getName(), salary);
            }
            case MID -> {
                salary -= salary * taxable.getMiddleTaxRate() / 100;
                developer = new MidDeveloper(developer.getId(), developer.getName(), salary);
            }
            case SENIOR -> {
                salary -= salary * taxable.getUpperTaxRate() / 100;
                developer = new SeniorDeveloper(developer.getId(), developer.getName(), salary);
            }
        }
        developers.put(developer.getId(), developer);
        return ResponseEntity.status(HttpStatus.CREATED).body(developer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Developer updated) {
        if (!developers.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Developer not found");
        }
        updated.setId(id);
        developers.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        if (!developers.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Developer not found");
        }
        developers.remove(id);
        return ResponseEntity.ok("Developer deleted");
    }
}
