package libs_kernel.mapper;

public interface EntityDetailMapper<T, TI> {
    TI toDetail(T entity);
}
