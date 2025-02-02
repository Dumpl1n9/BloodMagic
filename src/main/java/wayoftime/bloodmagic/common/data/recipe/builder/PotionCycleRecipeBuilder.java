package wayoftime.bloodmagic.common.data.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import wayoftime.bloodmagic.common.data.recipe.BloodMagicRecipeBuilder;
import wayoftime.bloodmagic.recipe.flask.RecipePotionFill;
import wayoftime.bloodmagic.util.Constants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PotionCycleRecipeBuilder extends BloodMagicRecipeBuilder<PotionCycleRecipeBuilder>
{
	private final List<Ingredient> input;
	private final int numCycles;
	private final int syphon;
	private final int ticks;
	private final int minimumTier;

	protected PotionCycleRecipeBuilder(List<Ingredient> input, int numCycles, int syphon, int ticks, int minimumTier)
	{
		super(bmSerializer("flask_potioncycle"));
		this.input = input;
		this.numCycles = numCycles;
		this.syphon = syphon;
		this.ticks = ticks;
		this.minimumTier = minimumTier;
	}

	public static PotionCycleRecipeBuilder potionCycle(int numCycles, int syphon, int ticks, int minimumTier)
	{
		List<Ingredient> inputList = new ArrayList<Ingredient>();

		return new PotionCycleRecipeBuilder(inputList, numCycles, syphon, ticks, minimumTier);
	}

	public PotionCycleRecipeBuilder addIngredient(Ingredient ing)
	{
		if (input.size() < RecipePotionFill.MAX_INPUTS)
		{
			input.add(ing);
		}

		return this;
	}

	@Override
	protected PotionFillRecipeResult getResult(ResourceLocation id)
	{
		return new PotionFillRecipeResult(id);
	}

	public class PotionFillRecipeResult extends RecipeResult
	{
		protected PotionFillRecipeResult(ResourceLocation id)
		{
			super(id);
		}

		@Override
		public void serializeRecipeData(@Nonnull JsonObject json)
		{
			if (input.size() > 0)
			{
				JsonArray mainArray = new JsonArray();
				for (Ingredient ing : input)
				{
					JsonElement jsonObj = ing.toJson();

					mainArray.add(jsonObj);
				}

				json.add(Constants.JSON.INPUT, mainArray);
			}

			json.addProperty(Constants.JSON.COUNT, numCycles);

			json.addProperty(Constants.JSON.SYPHON, syphon);
			json.addProperty(Constants.JSON.TICKS, ticks);
			json.addProperty(Constants.JSON.ALTAR_TIER, minimumTier);
		}
	}
}
