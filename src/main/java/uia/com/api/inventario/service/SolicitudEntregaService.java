package uia.com.api.inventario.service;

import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;
import uia.com.api.inventario.dto.CategoriaDTO;
import uia.com.api.inventario.dto.SolicitudEntregaDTO;
import uia.com.api.inventario.model.*;
import uia.com.api.inventario.repository.*;

import java.util.ArrayList;
import java.util.Optional;


@Service
public class SolicitudEntregaService<solicitudEntrega> {
    SolicitudEntregaRepository repositorySolicitudEntrega;
    ItemRepository repositoryItem;

    Validator validator;
    private SolicitudMaterialRepository repositorySolicitudMaterial;

    @Autowired
    public SolicitudEntregaService(SolicitudEntregaRepository repositorySolicitudEntrega,
                                   SolicitudMaterialRepository repositorySolicitudMaterial,
                                   ItemRepository repositoryItem,
                                   Validator validator) {
        this.repositorySolicitudEntrega = repositorySolicitudEntrega;
        this.repositorySolicitudMaterial = repositorySolicitudMaterial;
        this.repositoryItem = repositoryItem;
        this.validator = validator;
    }

    public SolicitudEntrega save(SolicitudEntregaDTO solicitud) {
        return saveInformation(solicitud);
    }

    public SolicitudEntrega update(SolicitudEntregaDTO solicitud) {
        return saveInformation(solicitud);
    }

    private SolicitudEntrega saveInformation(SolicitudEntregaDTO solicitudInDTO) {
        SolicitudEntregaDTO solicitudes_salvados = new SolicitudEntregaDTO();
        ArrayList<Item> itemsApartados = new ArrayList<Item>();
        SolicitudEntrega solicitudEntrega = new SolicitudEntrega();
        int itemDisponibles = 0;
        Optional<SolicitudMaterial> solicitudMaterialBD = null;

        if (this.repositorySolicitudMaterial.existsById(solicitudInDTO.getId())) {
            //-- Se toma el 0 como el unico que debe haber
            solicitudMaterialBD = this.repositorySolicitudMaterial.findById(solicitudInDTO.getId());
            for (int k = 0; k < solicitudMaterialBD.get().getItems().size(); k++) {

                if (solicitudMaterialBD.get().getItems().get(k).getEstatus().contentEquals("Entregado")) {
                    solicitudMaterialBD.get().getItems().get(k).setEstatus("Entregado");
                    itemsApartados.add(solicitudMaterialBD.get().getItems().get(k));
                }
            }
            ++itemDisponibles;
        }

        if (itemDisponibles > 0) {
            for (int j = 0; j < itemsApartados.size(); j++) {
                this.repositoryItem.save(itemsApartados.get(j));
            }
            solicitudEntrega.setItems(itemsApartados);
            solicitudEntrega.setId("SEM-1");
            solicitudEntrega.setName("Pedro Perez");
            solicitudEntrega.setEstatus("Entregada");
            solicitudEntrega.setClase("SEM");
            solicitudEntrega.setCantidad(String.valueOf(itemsApartados.size()));
            repositorySolicitudEntrega.save(solicitudEntrega);

        }


        return solicitudEntrega;
    }


}
