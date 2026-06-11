package libs_kernel.mapper;

public interface CommandMapper<C, T> {
    T toTarget(C command);
}
