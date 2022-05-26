package wayoftime.bloodmagic.common.recipe.serializer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import wayoftime.bloodmagic.potion.BloodMagicPotions;
import wayoftime.bloodmagic.recipe.flask.RecipePotionIncreasePotency;
import wayoftime.bloodmagic.util.Constants;

public class PotionIncreasePotencyRecipeSerializer<RECIPE extends RecipePotionIncreasePotency> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RECIPE>
{
	private final IFactory<RECIPE> factory;

	public PotionIncreasePotencyRecipeSerializer(IFactory<RECIPE> factory)
	{
		this.factory = factory;
	}

	@Nonnull
	@Override
	public RECIPE read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json)
	{
		List<Ingredient> inputList = new ArrayList<Ingredient>();

		if (json.has(Constants.JSON.INPUT) && JSONUtils.isJsonArray(json, Constants.JSON.INPUT))
		{
			JsonArray mainArray = JSONUtils.getJsonArray(json, Constants.JSON.INPUT);

			arrayLoop: for (JsonElement element : mainArray)
			{
				if (inputList.size() >= RecipePotionIncreasePotency.MAX_INPUTS)
				{
					break arrayLoop;
				}

				if (element.isJsonArray())
				{
					element = element.getAsJsonArray();
				} else
				{
					element.getAsJsonObject();
				}

				inputList.add(Ingredient.deserialize(element));
			}
		}

//		ItemStack output = SerializerHelper.getItemStack(json, Constants.JSON.OUTPUT);

		int syphon = JSONUtils.getInt(json, Constants.JSON.SYPHON);
		int ticks = JSONUtils.getInt(json, Constants.JSON.TICKS);
		int minimumTier = JSONUtils.getInt(json, Constants.JSON.ALTAR_TIER);

		Effect outputEffect = BloodMagicPotions.getEffect(new ResourceLocation(JSONUtils.getString(json, Constants.JSON.EFFECT)));
		int amplified = JSONUtils.getInt(json, Constants.JSON.AMPLIFIER);
		double ampDurationMod = JSONUtils.getFloat(json, Constants.JSON.AMP_DUR_MOD);

		return this.factory.create(recipeId, inputList, outputEffect, amplified, ampDurationMod, syphon, ticks, minimumTier);
	}

	@Override
	public RECIPE read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer)
	{
		try
		{
			int size = buffer.readInt();
			List<Ingredient> input = new ArrayList<Ingredient>(size);

			for (int i = 0; i < size; i++)
			{
				input.add(i, Ingredient.read(buffer));
			}

			int syphon = buffer.readInt();
			int ticks = buffer.readInt();
			int minimumTier = buffer.readInt();

			Effect outputEffect = Effect.get(buffer.readInt());
			int amplifier = buffer.readInt();
			double ampDurationMod = buffer.readDouble();

			return this.factory.create(recipeId, input, outputEffect, amplifier, ampDurationMod, syphon, ticks, minimumTier);
		} catch (Exception e)
		{
			throw e;
		}
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer, @Nonnull RECIPE recipe)
	{
		try
		{
			recipe.write(buffer);
		} catch (Exception e)
		{
			throw e;
		}
	}

	@FunctionalInterface
	public interface IFactory<RECIPE extends RecipePotionIncreasePotency>
	{
		RECIPE create(ResourceLocation id, List<Ingredient> input, Effect outputEffect, int amplifier, double ampDurationMod, int syphon, int ticks, int minimumTier);
	}
}