package imd.ufrn.br.purposesong.view;

import java.util.Optional;

import imd.ufrn.br.purposesong.App;
import imd.ufrn.br.purposesong.database.RepositoryFactory;
import imd.ufrn.br.purposesong.database.inmemory.InMemoryUserRepositoryImpl;
import imd.ufrn.br.purposesong.entity.User;
import imd.ufrn.br.purposesong.use_case.CreateNewUser;
import imd.ufrn.br.purposesong.utils.UserAlerts;
import imd.ufrn.br.purposesong.view.session.UserStore;

public class RegisterViewModel {

    public boolean createNewUser(String name, String email, String password, boolean isVip) {
        // !Setting new user
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        if (isVip) {
            user.setVipUser();
        } else {
            user.setNormalUser();
        }

        // !Alert to user + confirmation
        if (UserAlerts.alertVerifyaddUser(user)) {
            // !Adding to dataBase
            var repo = RepositoryFactory.getUserRepository();
            new CreateNewUser(repo).execute(user);
            System.out.println("Novo usuário adicionado ao sistema!");
            this.back();
            return true;
        } else {
            return false;
        }
    }

    public void empty() {
        UserAlerts.alertEmpytUser();
    }

    private App app = App.getInstance();

    public void back() {
        if (Optional.ofNullable(UserStore.getInstance().getUser()).isPresent()) {
            this.goToMenu();
        } else {
            this.goToLogin();
        }
    }

    public void goToLogin() {
        this.app.changeToLoginScene();
    }

    public void goToMenu() {
        this.app.changeToMenuScene();
    }

    // Singleton -------------------
    private static final RegisterViewModel instance = new RegisterViewModel();

    private RegisterViewModel() {

    }

    public static RegisterViewModel getInstance() {
        return RegisterViewModel.instance;
    }
}
