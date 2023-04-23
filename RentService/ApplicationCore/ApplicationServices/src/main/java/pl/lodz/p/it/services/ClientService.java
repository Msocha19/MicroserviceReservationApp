package pl.lodz.p.it.services;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import pl.lodz.p.it.domain.exceptions.DatabaseException;
import pl.lodz.p.it.domain.model.Client;
import pl.lodz.p.it.ports.view.ClientServicePort;
import pl.lodz.p.it.domain.model.Reservation;
import pl.lodz.p.it.ports.repository.client.InClientPort;
import pl.lodz.p.it.ports.repository.client.OutClientPort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ClientService implements ClientServicePort {

    @Autowired
    private InClientPort inClientPort;

    @Autowired
    private OutClientPort outClientPort;

    private final RabbitTemplate rabbitTemplate;

    @Override
    @Timed(value = "client.creation.time", description = "Time taken to create a customer")
    public Client createClient(Client client) {
        try {
            return inClientPort.createClient(client);
        } catch (DatabaseException | CannotCreateTransactionException e) {
            JSONObject jsonObject = new JSONObject();
            UUID messageId = UUID.randomUUID();
            jsonObject.put("messageId", messageId);
            jsonObject.put("time", LocalDateTime.now());
            jsonObject.put("customerId", client.getId());
            jsonObject.put("method", "deleteCustomer");
            rabbitTemplate.convertAndSend("tks-exchange", "response.deleteCustomer", jsonObject.toString());
        }
        return null;
    }

    @Override
    @Counted(value = "get.client.byId")
    public Client getClientById(UUID id) {
        return outClientPort.getUser(id);
    }

    @Override
    @Counted(value = "get.client.reservations")
    public List<Reservation> getReservations(UUID id) {
        return this.getClientById(id).getReservations();
    }

    @Override
    @Counted(value = "get.client.futureReservations")
    public List<Reservation> getFutureReservations(UUID id) {
        return this.getReservations(id).stream().filter(r -> r.getEndDate().isAfter(LocalDate.now())).toList();
    }

    @Override
    @Counted(value = "get.client.pastReservations")
    public List<Reservation> getPastReservations(UUID id) {
        return this.getReservations(id).stream().filter(r -> r.getEndDate().isBefore(LocalDate.now())).toList();
    }

    @Override
    public Client getUserFromServerContext() {
        return (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    @Timed(value = "client.activation.time", description = "Time taken to activate a customer")
    public void activateClient(UUID id) {
        try {
            inClientPort.changeClientActivityClient(id, true);
        } catch(DatabaseException | CannotCreateTransactionException e) {
            JSONObject jsonObject = new JSONObject();
            UUID messageId = UUID.randomUUID();
            jsonObject.put("messageId", messageId);
            jsonObject.put("time", LocalDateTime.now());
            jsonObject.put("customerId", id);
            jsonObject.put("method", "deactivateCustomer");
            rabbitTemplate.convertAndSend("tks-exchange", "response.deactivateCustomer", jsonObject.toString());
        }
    }

    @Override
    @Timed(value = "client.deactivation.time", description = "Time taken to deactivate a customer")
    public void deactivateClient(UUID id) {
        try {
            inClientPort.changeClientActivityClient(id, false);
        } catch (DatabaseException | CannotCreateTransactionException e) {
            JSONObject jsonObject = new JSONObject();
            UUID messageId = UUID.randomUUID();
            jsonObject.put("messageId", messageId);
            jsonObject.put("time", LocalDateTime.now());
            jsonObject.put("customerId", id);
            jsonObject.put("method", "activateCustomer");
            rabbitTemplate.convertAndSend("tks-exchange", "response.activateCustomer", jsonObject.toString());
        }
    }
}