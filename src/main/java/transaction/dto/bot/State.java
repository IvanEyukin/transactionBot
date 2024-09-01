package transaction.dto.bot;

import lombok.Data;
import transaction.entitie.Status;

@Data
public class State {
    private Status state;
    private Status previousState;

    public void updateState(Status state) {
        this.previousState = this.state;
        this.state = state;
    }
}