package io.github.lucaargolo.kibe.recipes.vacuum

import alexiil.mc.lib.attributes.Simulation
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount
import io.github.lucaargolo.kibe.blocks.vacuum.VacuumHopperEntity
import io.github.lucaargolo.kibe.fluids.LIQUID_XP
import io.github.lucaargolo.kibe.recipes.VACUUM_HOPPER_RECIPE_SERIALIZER
import io.github.lucaargolo.kibe.recipes.VACUUM_HOPPER_RECIPE_TYPE
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class VacuumHopperRecipe(private val id: Identifier, val ticks: Int, val xpInput: Long, val input: Ingredient, private val output: ItemStack) : Recipe<VacuumHopperEntity> {

    override fun getId() = id

    override fun matches(inv: VacuumHopperEntity, world: World): Boolean {
        val inputStack = inv.getStack(9)
        val inputVolume = inv.getTank(0).attemptAnyExtraction(FluidAmount.of(xpInput, 1000), Simulation.SIMULATE)
        val hasSpace = inv.getStack(10).let {
            it.isEmpty || (ItemStack.areItemsEqual(it, output) && ItemStack.areNbtEqual(it, output) && it.count < it.maxCount)
        }
        return input.test(inputStack) && inputVolume == LIQUID_XP.key.withAmount(FluidAmount.of(xpInput, 1000)) && hasSpace
    }

    override fun craft(inv: VacuumHopperEntity): ItemStack {
        inv.getStack(9).decrement(1)
        inv.getTank(0).extract(FluidAmount.of(xpInput, 1000))
        if(inv.getStack(10).isEmpty) {
            inv.setStack(10, output.copy())
        }else{
            inv.getStack(10).increment(1)
        }
        inv.markDirty()
        return output.copy()
    }

    override fun getType() = VACUUM_HOPPER_RECIPE_TYPE

    override fun fits(width: Int, height: Int) = true

    override fun getSerializer() = VACUUM_HOPPER_RECIPE_SERIALIZER

    override fun getOutput(): ItemStack = output

    override fun createIcon(): ItemStack = Items.EXPERIENCE_BOTTLE.defaultStack

    override fun getIngredients(): DefaultedList<Ingredient> = DefaultedList.ofSize(1, input)

}