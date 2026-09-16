package com.unicofrance.uniexo.ui.container

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicofrance.uniexo.R
import com.unicofrance.uniexo.data.local.database.entities.Container
import com.unicofrance.uniexo.ui.components.DefaultSpacer
import com.unicofrance.uniexo.ui.lib.SvgIcon
import com.unicofrance.uniexo.utils.getDateTime

/*
*  Screen for container information
*
* */
@Composable
fun ContainerScreen(
    container : Container,
    containerState : MutableIntState
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(110.dp))
        ContainerTitle(
            container = container,
            containerState = containerState
        )
        Column(
            modifier = Modifier
                .padding(30.dp)
        ) {
            ContainerTextField(
                contentText = container.id,
                labelText = "ID"
            )
            DefaultSpacer()
            Row {
                ContainerTextField(
                    contentText = container.latitude.toString(),
                    labelText = "Latitude",
                    modifier = Modifier.weight(1f)
                )
                Spacer(
                    modifier = Modifier
                        .width(10.dp)
                )
                ContainerTextField(
                    contentText = container.longitude.toString(),
                    labelText = "Longitude",
                    modifier = Modifier.weight(1f)
                )
            }
            DefaultSpacer()
            ContainerTextField(
                contentText = container.streamLabel,
                labelText = "Flux"
            )
            DefaultSpacer()
            ContainerTextField(
                contentText = getDateTime(container.creationDatetime),
                labelText = "Date de création"
            )
        }
    }
}

@Composable
fun ContainerTitle(
    container: Container,
    containerState: MutableIntState
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            SvgIcon(
                url = container.iconUrl,
                modifier = Modifier.size(100.dp)
            )
            Text(
                text = container.description,
                fontSize = 20.sp
            )
        }

        IconButton(
            onClick = {
                containerState.intValue = 0
            },
            modifier = Modifier.align(Alignment.Top)
        ) {
            Icon(
                painter = painterResource(R.drawable.close_icon),
                contentDescription = "Fermer la page",
                modifier = Modifier.size(80.dp)
            )
        }
    }
}


@Composable
fun ContainerTextField(
    contentText: String,
    labelText : String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .height(77.dp)
            .fillMaxWidth()
            .background(
                colorResource(R.color.second_background)
            )
            .border(
                width = 1.dp,
                color = colorResource(R.color.strokes),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(
                horizontal = 30.dp,
                vertical = 15.dp
            )
    ) {
        Text(
            text = labelText,
            fontSize = 16.sp,
            color = colorResource(R.color.non_selected_icon),
        )
        Text(
            text = contentText,
            fontSize = 20.sp,
            color = colorResource(R.color.title)
        )
    }
}
