package org.meditrack.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.meditrack.app.entity.Paitent;
import org.meditrack.app.exceptions.PaitentNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class PaitentService {

    List<Paitent> paitents;
    public PaitentService() {
        paitents = new ArrayList<>();
    }
    public List<Paitent> getPaitents() {
        return paitents;
    }
    public void setPaitents(List<Paitent> paitents) {
        this.paitents = paitents;
    }
    public Paitent getPaitent(String MRN) throws PaitentNotFoundException {
        for (Paitent paitent : paitents) {
            if(paitent.getMRN().equals(MRN)){
                return paitent;
            }
        }
        throw new PaitentNotFoundException("Paitent not found with MRN: " + MRN);
    };

    public void addPaitent(Paitent paitent) {
        paitents.add(paitent);
    }

    public void deletePaitent(String MRN) throws PaitentNotFoundException {
        try {
            paitents.remove(getPaitent(MRN));
        } catch (PaitentNotFoundException e) {
            throw new PaitentNotFoundException("Paitent with MRN: " + MRN + "does not exist");
        }
    }

    public void updatePaitent(String MRN, String PaitentObject) throws PaitentNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Paitent updatedPaitent = mapper.readValue(PaitentObject,Paitent.class);
        Paitent existingPaitent = getPaitent(MRN);

        // Now update fields — only the ones you want to change
        existingPaitent.setName(updatedPaitent.getName());
        existingPaitent.setAge(updatedPaitent.getAge());
        existingPaitent.setAddress(updatedPaitent.getAddress() == null ? null : updatedPaitent.getAddress());
        existingPaitent.setContactNo(updatedPaitent.getContactNo());
        existingPaitent.setAddress(updatedPaitent.getAddress());
        existingPaitent.setBloodGroup(updatedPaitent.getBloodGroup());
        existingPaitent.setKnownAllergies(updatedPaitent.getKnownAllergies());
        existingPaitent.setChronicConditions(updatedPaitent.getChronicConditions());
        existingPaitent.setCurrentMedications(updatedPaitent.getCurrentMedications());
    }
}
