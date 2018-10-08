package controllers;

import dao.BuyerDAO;
import dao.PersonDAO;
import dao.SellerDAO;
import dao.UserDAO;
import enums.Status;
import enums.UserRoles;
import models.Buyer;
import models.Person;
import models.Seller;
import models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = "/auth")
public class AuthController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AuthController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        session.removeAttribute("loggedUser");
        session.removeAttribute("loggedPerson");
        response.sendRedirect("/");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action.equals("signin")) {
            signin(request, response);
        } else if (action.equals("register")) {
            register(request, response);
        }
    }

    private void signin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = new User(request.getParameter("email"));
        User loggedUser = new UserDAO(true).checkIfExists(user);

        if (loggedUser == null || !loggedUser.checkPassword(request.getParameter("password"))) {
            request.setAttribute("error", "Email ou senha inválida");
            request.getRequestDispatcher("/signin").forward(request, response);
            return;
        }

        if (loggedUser.getStatus() == Status.INACTIVE) {
            request.setAttribute("error", "Cadastro desativado");
            request.getRequestDispatcher("/signin").forward(request, response);
            return;
        }

        Person loggedPerson = new Person();
        loggedPerson.setUser(loggedUser);
        loggedPerson = new PersonDAO(true).findByUser(loggedPerson);

        loggedUser.setPassword(null);
        HttpSession session = request.getSession();
        session.setAttribute("loggedUser", loggedUser);
        session.setAttribute("loggedPerson", loggedPerson);

        response.sendRedirect("/");

    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /**
         * Extrai os dígitos da String e faz o parse pra Long
         */
        try {
            Long tel = Long.parseLong(request.getParameter("tel").replaceAll("\\D+", ""));
            Long cnpj = Long.parseLong(request.getParameter("cnpj").replaceAll("\\D+", ""));

            Person person = new Person();
            person.setAccountOwner(request.getParameter("accountOwner"));
            person.setTel(tel);
            person.setCnpj(cnpj);
            person.setCorporateName(request.getParameter("corporateName"));
            person.setStateRegistration(Long.parseLong(request.getParameter("stateRegistration")));

            User user = new User();
            user.setEmail(request.getParameter("email"));
            user.setPassword(request.getParameter("password"));
            user.setUsername(request.getParameter("username"));
            user.setRole(UserRoles.COMMON);
            user.setStatus(Status.ACTIVE);

            user.generateDisplayName(person.getAccountOwner());
            user.hashPassword();

            UserDAO userDao = new UserDAO(true);
            userDao.initTransaction();
            user = userDao.create(user);

            person.setUser(user);

            PersonDAO personDao = new PersonDAO(userDao.getConnection());
            person = personDao.create(person);

            Buyer buyer = new Buyer();
            buyer.setId(person.getId());

            BuyerDAO buyerDao = new BuyerDAO(personDao.getConnection());
            buyerDao.create(buyer);

            Seller seller = new Seller();
            seller.setId(person.getId());
            seller.setQuotesExpirationPeriod(30); // Default: 30 days

            SellerDAO sellerDao = new SellerDAO(buyerDao.getConnection());
            sellerDao.create(seller);

            response.sendRedirect(request.getContextPath() + "/signin");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erro inesperado, tente mais tarde");
            request.getRequestDispatcher(request.getContextPath() + "/register").forward(request, response);
        }

    }
}
