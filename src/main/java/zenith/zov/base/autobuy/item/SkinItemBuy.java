package zenith.zov.base.autobuy.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Optional;
import java.util.UUID;

public class SkinItemBuy extends ItemBuy {
    private final String skin;
    public SkinItemBuy(String name,Category category, String skin) {
        this(skin, name, name, category);
    }

    public SkinItemBuy(String skin, String displayName, String searchName, Category maxSumBuy) {

        super(Items.PLAYER_HEAD.getDefaultStack(), displayName, searchName, maxSumBuy);
        this.skin = skin;
        final ComponentChanges.Builder datacomponentpatch$builder = ComponentChanges.builder();
        Optional<String> emptyString = Optional.of("");
        Optional<UUID> emptyUUID = Optional.of(UUID.randomUUID());
        Multimap<String, Property> properties = HashMultimap.create();
        properties.put("textures", new Property("textures", skin));
        PropertyMap propertyMap = new PropertyMap(properties);
        GameProfile gameProfile = new GameProfile(emptyUUID.orElse(UUID.randomUUID()), emptyString.orElse(""), propertyMap);
        ProfileComponent resolvableProfile = ProfileComponent.ofStatic(gameProfile);
        datacomponentpatch$builder.add(DataComponentTypes.PROFILE, resolvableProfile);
        ItemStackArgument input = new ItemStackArgument(itemStack.getRegistryEntry(), datacomponentpatch$builder.build());
        try {
            this.itemStack = input.createStack(1, false);
        } catch (CommandSyntaxException e) {

        }

    }

    @Override
    public boolean isBuy(ItemStack stack) {
        if (!super.isBuy(stack)) {
            return false;
        }

        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null && customData.copyNbt().contains("SkullOwner")) {
            return customData.copyNbt().get("SkullOwner").toString().contains(this.skin);

        }
        return false;

    }
    
    public String getSkin() {
        return skin;
    }
  }
