package com.simform.ssfurnicraftar.ui.arview

import android.net.Uri
import com.google.ar.core.Config
import com.simform.ssfurnicraftar.ui.arview.component.FurnitureDimensions

data class ARViewUiState(
    val productId: String,
    val modelPath: String,
    val findingMode: Config.PlaneFindingMode = Config.PlaneFindingMode.HORIZONTAL,
    val modelColor: ColorState = ColorState.None,
    val shareUri: Uri? = null,
    val currentDimensions: FurnitureDimensions? = null,
    val showDimensions: Boolean = false
)
