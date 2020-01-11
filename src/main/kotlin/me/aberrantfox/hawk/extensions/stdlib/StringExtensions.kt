package me.aberrantfox.hawk.extensions.stdlib

fun String.inject(element: String) =
         replace("%1%", element)

fun String.inject(element: String, element2: String) =
         replace("%1%", element)
        .replace("%2%", element2)