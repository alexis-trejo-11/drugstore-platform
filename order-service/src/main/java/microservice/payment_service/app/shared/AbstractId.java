package microservice.payment_service.app.shared;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public abstract class AbstractId {
    private final UUID value;

    public AbstractId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ID value cannot be null");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractId that = (AbstractId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
