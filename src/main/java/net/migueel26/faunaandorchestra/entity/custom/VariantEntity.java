package net.migueel26.faunaandorchestra.entity.custom;

public interface VariantEntity<T> {
    public int getVariantId();
    public T getVariant();
    public void setVariant(T variant);
}
