package com.project.fotoalbum.service;

import com.project.fotoalbum.dto.FotoForm;
import com.project.fotoalbum.exceptions.FotoIsRequiredException;
import com.project.fotoalbum.exceptions.FotoNotFoundException;
import com.project.fotoalbum.models.Foto;
import com.project.fotoalbum.models.Role;
import com.project.fotoalbum.models.User;
import com.project.fotoalbum.repository.FotoRepository;
import com.project.fotoalbum.repository.UserRepository;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FotoService {

@Autowired
FotoRepository fotoRepository;
@Autowired
Environment env;
@Autowired
UserRepository userRepository;

// Ottieni tutte le foto con query param opzionale e booleano visibile
    public List<Foto> getAll(boolean visible, Optional<String> keyword) {
        List<Foto> fotoList = null;
        if (visible) { // Se la richiesta è per le foto visibili
            if (keyword.isPresent()) // Se c'è una keyword filtro per la keyword fornita
                fotoList = fotoRepository.findByTitleContainingIgnoreCaseAndVisibleTrue(keyword.get());
            if (keyword.isEmpty())
                fotoList = fotoRepository.findByVisibleTrue(); // Altrimenti invio tutte le foto visibili
        } else { // Se la richiesta è per visible false
            if (keyword.isPresent()) // Se c'è una keyword filtro per quella
                fotoList = fotoRepository.findByTitleContainingIgnoreCase(keyword.get());
            if (keyword.isEmpty()) // Altrimenti invio tutte le foto
                fotoList = fotoRepository.findAll();
        }
        return fotoList;
    }

    // Ottieni le foto dell'utente corrente
    public List<Foto> getByActiveUser(String authUser, Optional<String> keyword) {
        User authUsername = userRepository.findByEmail(authUser).get();
        if(isAdmin(authUser)) {
            // Se l'utente loggato è un admin ritorno tutte le foto
                if (keyword.isPresent()) return getAll(false, keyword); // Filtrate se c'è una keyword
                else return getAll(false, Optional.empty());
        } // Altrimenti ritorno le foto dell'utente loggato
        if(keyword.isPresent()) return fotoRepository.findByUserIdAndTitleContainingIgnoreCase(authUsername.getId(), keyword.get());
        else return fotoRepository.findByUserId(authUsername.getId());
    }

    // Ottieni foto specifica
    public Foto getById(Integer id) throws FotoNotFoundException {
        Foto foto = null;
        if (fotoExists(id)) foto = fotoRepository.findById(id).get();
        return foto;
    }

    public Foto getByIdWithAuth(Integer id, String authUser) throws AuthException {
        User authUsername = userRepository.findByEmail(authUser).get();
        if(isAdmin(authUser)) getById(id); // Se l'utente è un admin, skip del controllo di sicurezza
        Foto foto = null;
        if (fotoExists(id)) foto = fotoRepository.findById(id).get();
        // Se l'username associato alla foto non è presente oppure non è identico all'username dell'utente loggato, lancio errore
        if (foto.getUser() == null || !foto.getUser().getEmail().equals(authUsername.getEmail())) throw new AuthException("Utente non autorizzato a visualizzare l'elemento");
        return foto;
    }

    public Foto create(Foto foto) {
        Foto fotoToSave = new Foto();
        fotoToSave.setCreatedAt(LocalDateTime.now());
        fotoToSave.setCategories(foto.getCategories());
        fotoToSave.setDescription(foto.getDescription());
        fotoToSave.setTitle(foto.getTitle());
        fotoToSave.setVisible(foto.isVisible());
        if (foto.getUser() != null) fotoToSave.setUser(foto.getUser());
        if (foto.getDBimage() != null) fotoToSave.setDBimage(foto.getDBimage());
        fotoRepository.save(fotoToSave);
        if(fotoToSave.getDBimage() != null) fotoToSave.setPictureUrl(env.getProperty("localpath") + "/files/" + fotoToSave.getId());
        else fotoToSave.setPictureUrl(foto.getPictureUrl());
        return fotoRepository.save(fotoToSave);
    }

    public Foto create(FotoForm fotoForm, String authUser) throws FotoIsRequiredException {
        Foto fotoToSave = fromFotoFormToFoto(fotoForm);
        if(!isAdmin(authUser)) { // Se l'utente non è admin, allora lo setto come proprietario della foto
            User user = userRepository.findByEmail(authUser).get();
            fotoToSave.setUser(user);
        }
        if (fotoToSave.getDBimage() == null && fotoToSave.getPictureUrl().isBlank()) {
            throw new FotoIsRequiredException("Sono necessari la url oppure l'upload del file dell'immagine");
        }
        return create(fotoToSave);
    }

    public Foto edit(Foto foto) throws FotoNotFoundException {
        Foto fotoToUpdate = getById(foto.getId()); // Recupero la foto da modificare
        fotoToUpdate.setUpdatedAt(LocalDateTime.now());
        fotoToUpdate.setCategories(foto.getCategories());
        fotoToUpdate.setDescription(foto.getDescription());
        fotoToUpdate.setTitle(foto.getTitle());
        fotoToUpdate.setVisible(foto.isVisible());
        // Se è presente la DB image, setta la URL della foto con il path del file locale
        if(foto.getDBimage() != null) fotoToUpdate.setPictureUrl(env.getProperty("localpath") + "/files/" + fotoToUpdate.getId());
        else fotoToUpdate.setPictureUrl(foto.getPictureUrl());
        return fotoRepository.save(fotoToUpdate);
    }

    public Foto edit(FotoForm foto, String authUser) throws FotoIsRequiredException, AuthException {
        Foto fotoDb = getByIdWithAuth(foto.getId(), authUser);
        Foto fotoToSave = fromFotoFormToFoto(foto);
        if (fotoToSave.getDBimage() == null && fotoToSave.getPictureUrl().isBlank()) {
            throw new FotoIsRequiredException("Sono necessari la url oppure l'upload del file dell'immagine");
        }
        // Setta la db image solo se presente
        if(fotoToSave.getDBimage() != null) fotoDb.setDBimage(fotoToSave.getDBimage());
        fotoDb.setCategories(fotoToSave.getCategories());
        fotoDb.setDescription(fotoToSave.getDescription());
        fotoDb.setTitle(fotoToSave.getTitle());
        fotoDb.setVisible(fotoToSave.isVisible());
        // Se è presente la DB image, setta la URL della foto con il path del file locale
        if(fotoToSave.getDBimage() != null) fotoDb.setPictureUrl(env.getProperty("localpath") + "/files/" + foto.getId());
        else fotoDb.setPictureUrl(foto.getPictureUrl());
        fotoDb.setUpdatedAt(LocalDateTime.now());
        return fotoRepository.save(fotoDb);
    }

    public void delete(Integer id, String authUser) throws FotoNotFoundException, AuthException {
        if(isAdmin(authUser)) fotoRepository.delete(getById(id));
        else {
            Foto foto = fotoRepository.findById(id).get();
            // Se l'email dell'utente loggato non è uguale all'username, l'utente non è autorizzato
            if(!foto.getUser().getEmail().equals(authUser)) throw new AuthException("Utente non autorizzato all'operazione");
            fotoRepository.delete(getById(id));
        }
    }

    // CHECK SE UTENTE E' ADMIN
    public boolean isAdmin(String authUser) {
        User authUsername = userRepository.findByEmail(authUser).get();
        Set<Role> authUserRoles = authUsername.getRoles();
        for (Role role : authUserRoles) {
            if (role.getName().equals("ADMIN")) return true;
            }
        return false;

    }

    // METODI DI CHECK E TASFORMAZIONE DTO
    private boolean fotoExists(Integer id) throws FotoNotFoundException {
        Optional<Foto> foundFoto = fotoRepository.findById(id);
        if (foundFoto.isPresent()) return true;
        else throw new FotoNotFoundException();
    }

    public Foto fromFotoFormToFoto(FotoForm fotoForm) {
        Foto fotoToSave = new Foto();
        fotoToSave.setTitle(fotoForm.getTitle());
        fotoToSave.setVisible(fotoForm.isVisible());
        fotoToSave.setDescription(fotoForm.getDescription());
        fotoToSave.setPictureUrl(fotoForm.getPictureUrl());
        fotoToSave.setCategories(fotoForm.getCategories());
        // Invoco il metodo custom per conversione in bytes array
        if(fotoForm.getImageFile() != null) {
            fotoToSave.setDBimage(multipartFileToByteArray(fotoForm.getImageFile()));
        }
        return fotoToSave;
    }

    public FotoForm fromFotoToFotoForm(Foto foto) {
        FotoForm fotoForm = new FotoForm();
        fotoForm.setId(foto.getId());
        fotoForm.setCreatedAt(foto.getCreatedAt());
        fotoForm.setTitle(foto.getTitle());
        fotoForm.setDescription(foto.getDescription());
        fotoForm.setPictureUrl(foto.getPictureUrl());
        fotoForm.setVisible(foto.isVisible());
        fotoForm.setCategories(foto.getCategories());
        return fotoForm;
    }

    // Metodo per convertire un file multipart ricevuto da un form in un array di bytes
    private byte[] multipartFileToByteArray(MultipartFile file) {
        byte[] bytes = null;
        if (file != null && !file.isEmpty()) {
            try {
                bytes = file.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }
}
