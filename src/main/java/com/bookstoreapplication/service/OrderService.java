package com.bookstoreapplication.service;

import com.bookstoreapplication.dto.OrderDTO;
import com.bookstoreapplication.exception.BookStoreException;
import com.bookstoreapplication.model.Book;
import com.bookstoreapplication.model.Order;
import com.bookstoreapplication.model.UserRegistration;
import com.bookstoreapplication.repository.BookStoreRepository;
import com.bookstoreapplication.repository.OrderRepository;
import com.bookstoreapplication.repository.UserRegistrationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
/**
 * Created OrderService class to serve api calls done by controller layer
 */
public class OrderService implements IOrderService {
    /**
     * Autowired OrderRepository interface to inject its dependency here
     */
    @Autowired
    private OrderRepository orderRepo;
    /**
     * Autowired BookStoreRepository interface to inject its dependency here
     */
    @Autowired
    private BookStoreRepository bookRepo;

    @Autowired
    private UserRegistrationRepository userRepo;

    public Order insertOrder(OrderDTO orderdto) {
        Optional<Book> book = bookRepo.findById(orderdto.getBookId());
        Optional<UserRegistration> user = userRepo.findById(orderdto.getUserId());
        if (book.isPresent() && user.isPresent()) {
            if (orderdto.getQuantity()<= book.get().getQuantity()) {
                int quantity = book.get().getQuantity()-orderdto.getQuantity();
                book.get().setQuantity(quantity);
                bookRepo.save(book.get());
                Order newOrder = new Order(book.get().getPrice(), orderdto.getQuantity(), orderdto.getAddress(), book.get(), user.get(), orderdto.isCancel());
                orderRepo.save(newOrder);
                log.info("Order record inserted successfully");
                return newOrder;
            } else {
                throw new BookStoreException("Requested quantity is not available");
            }
        } else {
            throw new BookStoreException("Book or User doesn't exists");
        }
    }


    public List<Order> getAllOrderRecords() {
        List<Order> orderList = orderRepo.findAll();
        log.info("ALL order records retrieved successfully");
        return orderList;
    }


    public Order getOrderRecord(Integer id) {
        Optional<Order> order = orderRepo.findById(id);
        if (order.isPresent()) {
            log.info("Order record retrieved successfully for id " + id);
            return order.get();

        } else {
            throw new BookStoreException("Order Record doesn't exists");
        }
    }


    public Order updateOrderRecord(Integer id, OrderDTO dto) {
        Optional<Order> order = orderRepo.findById(id);
        Optional<Book> book = bookRepo.findById(dto.getBookId());
        Optional<UserRegistration> user = userRepo.findById(dto.getUserId());
        if (order.isPresent()) {
            if (book.isPresent() && user.isPresent()) {
                if (dto.getQuantity() <= book.get().getQuantity()) {
                    int quantity = book.get().getQuantity()-dto.getQuantity();
                    book.get().setQuantity(quantity);
                    bookRepo.save(book.get());
                    Order newOrder = new Order(id, book.get().getPrice(), dto.getQuantity(), dto.getAddress(), book.get(), user.get(), dto.isCancel());
                    orderRepo.save(newOrder);
                    log.info("Order record updated successfully for id " + id);
                    return newOrder;
                } else {
                    throw new BookStoreException("Requested quantity is not available");
                }
            } else {
                throw new BookStoreException("Book or User doesn't exists");

            }

        } else {
            throw new BookStoreException("Order Record doesn't exists");
        }
    }


    public Order deleteOrderRecord(Integer id) {
        Optional<Order> order = orderRepo.findById(id);
        if (order.isPresent()) {
            orderRepo.deleteById(id);
            log.info("Order record deleted successfully for id " + id);
            return order.get();

        } else {
            throw new BookStoreException("Order Record doesn't exists");
        }
    }
}
