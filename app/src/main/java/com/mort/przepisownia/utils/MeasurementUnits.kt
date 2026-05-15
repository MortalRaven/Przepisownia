package com.mort.przepisownia.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.mort.przepisownia.R

enum class UnitType {
    PIECE,

    GRAM,
    DAG,
    KILOGRAM,

    MILLILITER,
    LITER,

    TEASPOON,
    TABLESPOON,
    CUP,

    OUNCE_FLUID,
    OUNCE_WEIGHT
}


@Composable
fun UnitType?.displayName(amount: Float): String {

    val isFraction = amount % 1F != 0F

    return when (this) {
        UnitType.PIECE -> stringResource(R.string.unit_piece)
        UnitType.GRAM -> stringResource(R.string.unit_gram)
        UnitType.DAG -> stringResource(R.string.unit_dag)
        UnitType.KILOGRAM -> stringResource(R.string.unit_kg)
        UnitType.MILLILITER -> stringResource(R.string.unit_ml)
        UnitType.LITER -> stringResource(R.string.unit_liter)

        UnitType.TEASPOON -> if (isFraction) {
            stringResource(R.string.unit_teaspoon_fraction)
        } else {
            pluralStringResource(R.plurals.unit_teaspoon, amount.toInt())
        }

        UnitType.TABLESPOON -> if (isFraction) {
            stringResource(R.string.unit_tablespoon_fraction)
        } else {
            pluralStringResource(R.plurals.unit_tablespoon, amount.toInt())
        }

        UnitType.CUP -> if (isFraction) {
            stringResource(R.string.unit_cup_fraction)
        } else {
            pluralStringResource(R.plurals.unit_cup,amount.toInt())
        }

        UnitType.OUNCE_FLUID -> if (isFraction) {
            stringResource(R.string.unit_ounce_fluid_fraction)
        } else {
            pluralStringResource(R.plurals.unit_ounce_fluid,amount.toInt())
        }

        UnitType.OUNCE_WEIGHT -> if (isFraction) {
            stringResource(R.string.unit_ounce_weight_fraction)
        } else {
            pluralStringResource(R.plurals.unit_ounce_weight,amount.toInt())
        }

        else -> ""
    }
}