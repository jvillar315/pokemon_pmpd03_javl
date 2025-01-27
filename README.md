# Proyecto Android: Pokémon Manager

## 1. Introducción
Este proyecto consiste en una aplicación Android para gestionar una Pokédex y una lista de Pokémon capturados. El objetivo principal es **permitir a los usuarios registrarse**, **autenticarse**, **consultar la Pokédex** y **almacenar sus Pokémon capturados** usando servicios de terceros como la PokéAPI y Firebase.

## 2. Características principales
- **Autenticación de usuarios**: Inicio de sesión y registro con email/contraseña (y Google, opcionalmente).
- **Pokédex**: Consulta en tiempo real a la [PokéAPI](https://pokeapi.co/) para obtener la lista y detalles de cada Pokémon.
- **Captura de Pokémon**: Al seleccionar un Pokémon en la Pokédex, se obtiene su información detallada y se guarda en Firebase.
- **Lista de Pokémon capturados**: Muestra los Pokémon capturados en una pestaña separada (con CardViews). Permite ver detalles y, si la opción está habilitada, eliminar Pokémon.
- **Ajustes**:
  - Opción para permitir o desactivar la eliminación de Pokémon.
  - Cambio de idioma (castellano/inglés).
  - Información "Acerca de" (versión, desarrollador).
  - Cerrar sesión.

## 3. Tecnologías utilizadas
- **Lenguaje**: Java
- **IDE**: Android Studio
- **Firebase Authentication**: Manejo de usuarios y autenticación.
- **Firebase Firestore**: Almacenamiento de Pokémon capturados por cada usuario.
- **Retrofit + GSON**: Para consumir la PokéAPI.
- **RecyclerView + CardView**: Para mostrar las listas de Pokémon (Pokédex y Capturados).
- **SharedPreferences**: Para gestionar opciones como el idioma y la eliminación de Pokémon.
- **Git & GitHub**: Control de versiones y repositorio remoto.

## 4. Instrucciones de uso
Abre la aplicacion y navega por el menú inferior. Utiliza el menu de settings para cambiar algunas configuraciones.


## 5. Conclusiones
Es una buena actividad para entrenar la: integración de APIs  con Retrofit, Firebase Authentication y Firestore, Gestión de configuraciones con SharedPreferences, Fragments,
Adapters y RecyclerView, etc.
