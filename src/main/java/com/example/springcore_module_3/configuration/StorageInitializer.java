package com.example.springcore_module_3.configuration;

import com.example.springcore_module_3.model.Trainee;
import com.example.springcore_module_3.model.Trainer;
import com.example.springcore_module_3.model.Training;
import com.example.springcore_module_3.model.TrainingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
public class StorageInitializer implements BeanPostProcessor{

    @Value("${trainee.storage.file}")
    private String traineeDataFile;

    @Value("${trainer.storage.file}")
    private String trainerDataFile;

    @Value("${training.storage.file}")
    private String trainingDataFile;

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        switch (beanName){
            case "traineeStorage" -> loadTrainees((Map<Long, Trainee>)  bean);
            case "trainerStorage" -> loadTrainers((Map<Long, Trainer>)  bean);
            case "trainingStorage" -> loadTrainings((Map<Long, Training>)  bean);
            default -> {}
        }
        return bean;
    }

    private void loadTrainees(Map<Long, Trainee> storage){

        forEachLine(traineeDataFile, (line)->{
            String[] p = line.split("\\|");
            Trainee trainee = new Trainee();
            trainee.setUserId(Long.valueOf(p[0]));
            trainee.setFirstName(p[1]);
            trainee.setLastName(p[2]);
            trainee.setUsername(p[3]);
            trainee.setPassword(p[4]);
            trainee.setActive(Boolean.parseBoolean(p[5]));
            trainee.setAddress(p[6]);
            trainee.setDateOfBirth(LocalDate.parse(p[7]));
            storage.put(trainee.getUserId(), trainee);
        });
        log.info("Loaded {} trainees from {}", storage.size(), traineeDataFile);
    }

    private void loadTrainers(Map<Long, Trainer> storage){
        forEachLine(trainerDataFile, (line)->{
            String[] p = line.split("\\|");
            Trainer trainer = new Trainer();
            trainer.setUserId(Long.valueOf(p[0]));
            trainer.setFirstName(p[1]);
            trainer.setLastName(p[2]);
            trainer.setUsername(p[3]);
            trainer.setPassword(p[4]);
            trainer.setActive(Boolean.parseBoolean(p[5]));
            trainer.setSpecialization(TrainingType.valueOf(p[6]));
            storage.put(trainer.getUserId(), trainer);
        });
        log.info("Loaded {} trainers from {}", storage.size(), trainerDataFile);
    }

    private void loadTrainings(Map<Long, Training> storage){
        forEachLine(trainingDataFile, (line)->{
            String[] p = line.split("\\|");
            Training training = new Training();
            training.setTrainingId(Long.valueOf(p[0]));
            training.setTraineeId(Long.valueOf(p[1]));
            training.setTrainerId(Long.valueOf(p[2]));
            training.setTrainingName(p[3]);
            training.setTrainingType(TrainingType.valueOf(p[4]));
            training.setTrainingDate(LocalDate.parse(p[5]));
            training.setTrainingDuration(Duration.ofMinutes(Long.parseLong(p[6])));
            storage.put(training.getTrainingId(), training);
        });
        log.info("Loaded {} trainings from {}", storage.size(), trainingDataFile);
    }

    private void forEachLine(String classPathFile, Consumer<String> lineHandler){
        Resource resource = new ClassPathResource(classPathFile);
        if(!resource.exists()){
            log.warn("Storage data file {} not found on classpath", classPathFile);
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if(line.isEmpty() || line.startsWith("#")){
                    continue;
                }
                lineHandler.accept(line);
            }


        }catch (Exception e){
            log.error("Error while reading file {}", classPathFile, e);
            throw new IllegalStateException("Could not initialize storage" + classPathFile + " " +  e);
        }
    }

}
