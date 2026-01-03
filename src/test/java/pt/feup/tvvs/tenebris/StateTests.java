package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.controller.Controller;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.model.menu.Menu;
import pt.feup.tvvs.tenebris.savedata.SaveDataProvider;
import pt.feup.tvvs.tenebris.state.MenuState;
import pt.feup.tvvs.tenebris.state.StateChanger;
import pt.feup.tvvs.tenebris.view.View;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StateTests {
    @Test
    void testStateLogic() throws IOException, InterruptedException {
        Menu model = Mockito.mock(Menu.class);
        Controller<Menu> controller = Mockito.mock(Controller.class);
        View<Menu> view = Mockito.mock(View.class);

        when(model.getController()).thenReturn(controller);
        when(model.getView()).thenReturn(view);

        MenuState state = new MenuState(model);
        StateChanger changer = Mockito.mock(StateChanger.class);
        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);

        // Case 1: State did not change -> Draw
        when(changer.stateChanged()).thenReturn(false);
        state.tick(changer, provider);
        verify(view).draw();

        // Case 2: State changed -> Do not draw
        when(changer.stateChanged()).thenReturn(true);
        state.tick(changer, provider);
        // Verify view.draw() was not called a second time (invocation count 1)
        verify(view, Mockito.times(1)).draw();
    }
}