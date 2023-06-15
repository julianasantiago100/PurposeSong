package imd.ufrn.br.purposesong.view;

import imd.ufrn.br.purposesong.App;
import imd.ufrn.br.purposesong.database.inmemory.InMemoryUserRepositoryImpl;
import imd.ufrn.br.purposesong.entity.User;
import imd.ufrn.br.purposesong.use_case.CreateNewUser;
import imd.ufrn.br.purposesong.utils.UserAlerts;

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
            var repo = InMemoryUserRepositoryImpl.getInstance();
            new CreateNewUser(repo).execute(user);
            System.out.println("Novo usuário adicionado ao sistema!");
            this.goToMenu();
            return true;
        } else {
            return false;
        }
    }

    public void empty() {
        UserAlerts.alertEmpytUser();
    }

    private App app = App.getInstance();

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
