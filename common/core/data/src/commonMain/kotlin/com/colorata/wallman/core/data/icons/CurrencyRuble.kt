/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.colorata.wallman.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.CurrencyRuble: ImageVector
    get() {
        if (_currencyRuble != null) {
            return _currencyRuble!!
        }
        _currencyRuble = materialIcon(name = "Filled.CurrencyRuble") {
            materialPath {
                moveTo(13.5f, 3.0f)
                horizontalLineTo(7.0f)
                verticalLineToRelative(9.0f)
                horizontalLineTo(5.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(5.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(3.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-3.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineTo(9.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(4.5f)
                curveToRelative(3.04f, 0.0f, 5.5f, -2.46f, 5.5f, -5.5f)
                curveTo(19.0f, 5.46f, 16.54f, 3.0f, 13.5f, 3.0f)
                close()
                moveTo(13.5f, 12.0f)
                horizontalLineTo(9.0f)
                verticalLineTo(5.0f)
                horizontalLineToRelative(4.5f)
                curveTo(15.43f, 5.0f, 17.0f, 6.57f, 17.0f, 8.5f)
                reflectiveCurveTo(15.43f, 12.0f, 13.5f, 12.0f)
                close()
            }
        }
        return _currencyRuble!!
    }

private var _currencyRuble: ImageVector? = null
