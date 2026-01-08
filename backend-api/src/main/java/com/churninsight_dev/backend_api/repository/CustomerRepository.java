package com.churninsight_dev.backend_api.repository;

import com.churninsight_dev.backend_api.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    // Al extender de JpaRepository, ya tenemos disponibles métodos como:
    // .save(customer) -> Para guardar o actualizar
    // .findAll()      -> Para obtener la lista de todos los clientes
    // .findById(id)   -> Para buscar un cliente específico
    // .deleteById(id) -> Para eliminar

    /**
     * Ejemplo de método personalizado: buscar si existe un cliente por su ID.
     * Spring Data JPA implementa la lógica automáticamente basándose en el nombre del método.
     */
    boolean existsByCustomerId(String customerId);

    //Para que el Frontend pueda hacer el Dashboard
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.churned = true")
    long countChurnedCustomers();

}
