package net.migueel26.faunaandorchestra.advancements;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import org.jetbrains.annotations.NotNull;

public class CustomSimpleTrigger extends SimpleCriterionTrigger<CustomSimpleTrigger.Instance> {
    private final ResourceLocation id;

    public CustomSimpleTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    @Override
    protected @NotNull Instance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext context) {
        return new Instance(this.id, predicate);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public Instance(ResourceLocation id, ContextAwarePredicate predicate) {
            super(id, predicate);
        }
    }
}