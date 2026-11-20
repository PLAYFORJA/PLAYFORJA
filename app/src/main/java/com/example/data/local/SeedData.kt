package com.example.data.local

object SeedData {
    suspend fun populateIfEmpty(db: AppDatabase) {
        val userDao = db.userDao()
        if (userDao.countUsers() > 0) return

        val now = System.currentTimeMillis()
        val min = 60_000L
        val hr = 3_600_000L

        val passHash = SecurityUtils.createStoredPassword("playforja123")

        val u1 = userDao.insertUser(
            UserEntity(
                handle = "valkyria_gg",
                displayName = "Valkyria Nova",
                email = "valkyria@playforja.gg",
                passwordHash = passHash,
                bio = "IGL & Duelista en LATAM Sur. Construyendo setups extremos y compartiendo clips sin filtro.",
                customTitle = "Capitana de Escuadrón",
                platform = "PC",
                favoriteGame = "Valorant",
                avatarPreset = "preset_knight",
                bannerPreset = "banner_forge",
                frameStyle = FrameStyle.EMBER.key,
                accentTheme = "FORJA",
                status = GamerStatus.STREAMING.key,
                hardwareSpecs = "RTX 4080 Super • Ryzen 7 7800X3D • Wooting 60HE • 240Hz OLED",
                socialLinks = "Twitch: /valkyriagg • Discord: valkyria#01 • Steam: ValkyriaNova",
                clanTag = "FORJA",
                level = 24,
                xp = 5940,
                followersCount = 1480,
                followingCount = 215,
                badgesCsv = "Forjadora Élite,MVP Torneo S4,Streamer Verificada,Arquitecta RGB",
                isVerified = true,
                createdAt = now - 30 * 24 * hr,
                profileBgStyle = ProfileBgStyle.MAGMA.key,
                favoriteGamesCsv = "valorant,cs2,apex",
                bioMotto = "Cada ronda es una nueva historia.",
                playSchedule = "Noches 20:00 - 02:00"
            )
        )

        val u2 = userDao.insertUser(
            UserEntity(
                handle = "kuro_blade",
                displayName = "Kuro Tanaka",
                email = "kuro@playforja.gg",
                passwordHash = passHash,
                bio = "Speedrunner de Soulsborne y modder de RPGs. Si hay jefe imposible, se cae en No-Hit.",
                customTitle = "Cazador No-Hit",
                platform = "PS5",
                favoriteGame = "Elden Ring",
                avatarPreset = "preset_cyber",
                bannerPreset = "banner_cyber",
                frameStyle = FrameStyle.CYAN.key,
                accentTheme = "CIBER",
                status = GamerStatus.INGAME.key,
                hardwareSpecs = "PS5 Pro • DualSense Edge • LG C3 OLED 42\"",
                socialLinks = "YouTube: @KuroRuns • PSN: Kuro_Blade99",
                clanTag = "SOULS",
                level = 19,
                xp = 4680,
                followersCount = 920,
                followingCount = 134,
                badgesCsv = "Maestro Souls,Sin Daño,Creador de Guías",
                isVerified = true,
                createdAt = now - 25 * 24 * hr,
                profileBgStyle = ProfileBgStyle.CYBER_GRID.key,
                favoriteGamesCsv = "elden_ring,mhwilds,cyberpunk",
                bioMotto = "La paciencia derrota al jefe más duro.",
                playSchedule = "Fines de semana 24/7"
            )
        )

        val u3 = userDao.insertUser(
            UserEntity(
                handle = "sofia_pixel",
                displayName = "Sofía PixelArt",
                email = "sofia@playforja.gg",
                passwordHash = passHash,
                bio = "Ilustradora indie, diseñadora de mods y fanática de los metroidvania. Libertad creativa total.",
                customTitle = "Artista Ciberpunk",
                platform = "Switch",
                favoriteGame = "Hollow Knight",
                avatarPreset = "preset_plasma",
                bannerPreset = "banner_arena",
                frameStyle = FrameStyle.PLASMA.key,
                accentTheme = "PLASMA",
                status = GamerStatus.ONLINE.key,
                hardwareSpecs = "Nintendo Switch OLED • PC Cintiq 16 • Steam Deck OLED",
                socialLinks = "ArtStation: /sofiapixel • Itch.io: sofiapx",
                clanTag = "PIXEL",
                level = 16,
                xp = 3900,
                followersCount = 740,
                followingCount = 310,
                badgesCsv = "Forjadora Creativa,Modder Estrella,Coleccionista Indie",
                isVerified = false,
                createdAt = now - 20 * 24 * hr,
                profileBgStyle = ProfileBgStyle.VOID_NEBULA.key,
                favoriteGamesCsv = "hollow_knight,genshin,minecraft",
                bioMotto = "Arte, píxeles y mundos sin fronteras.",
                playSchedule = "Tardes y madrugadas"
            )
        )

        val u4 = userDao.insertUser(
            UserEntity(
                handle = "drako_rx",
                displayName = "Drako Montoya",
                email = "drako@playforja.gg",
                passwordHash = passHash,
                bio = "Overclocking, refrigeración líquida y competitivos FPS. Siempre buscando gente seria para ranked nocturno.",
                customTitle = "Ingeniero RGB",
                platform = "PC",
                favoriteGame = "Apex Legends",
                avatarPreset = "preset_venom",
                bannerPreset = "banner_cyber",
                frameStyle = FrameStyle.LIME.key,
                accentTheme = "VENOM",
                status = GamerStatus.ONLINE.key,
                hardwareSpecs = "RTX 4090 • i9-14900K Custom Loop • 64GB DDR5 • Logitech Superlight 2",
                socialLinks = "Discord: drako_rx • Steam: DrakoOC",
                clanTag = "APEX",
                level = 21,
                xp = 5150,
                followersCount = 1120,
                followingCount = 189,
                badgesCsv = "Maestro Hardware,Líder LFG,Racha 15 Victorias",
                isVerified = true,
                createdAt = now - 15 * 24 * hr,
                profileBgStyle = ProfileBgStyle.TOXIC_CIRCUIT.key,
                favoriteGamesCsv = "apex,warzone,rocket_league",
                bioMotto = "Más FPS, menos excusas.",
                playSchedule = "Diario 21:00 - 03:00"
            )
        )

        val u5 = userDao.insertUser(
            UserEntity(
                handle = "forja_oficial",
                displayName = "PlayForja HQ",
                email = "hq@playforja.gg",
                passwordHash = passHash,
                bio = "Cuenta oficial de la comunidad global PlayForja. Torneos semanales, noticias sin censura y retos de la comunidad.",
                customTitle = "Núcleo PlayForja",
                platform = "Multi",
                favoriteGame = "Torneos Globales",
                avatarPreset = "preset_gold",
                bannerPreset = "banner_forge",
                frameStyle = FrameStyle.GOLD.key,
                accentTheme = "ORO",
                status = GamerStatus.ONLINE.key,
                hardwareSpecs = "Servidores Globales Low-Ping • 128Hz Tickrate",
                socialLinks = "PlayForja.gg • Torneos Abiertos 24/7",
                clanTag = "HQ",
                level = 50,
                xp = 12450,
                followersCount = 9850,
                followingCount = 12,
                badgesCsv = "Núcleo Oficial,Anfitrión Global,Forja Suprema",
                isVerified = true,
                createdAt = now - 40 * 24 * hr,
                profileBgStyle = ProfileBgStyle.GOLD_ROYAL.key,
                favoriteGamesCsv = "valorant,lol,fortnite,eafc25",
                bioMotto = "El hogar libre de todos los gamers.",
                playSchedule = "Torneos 24/7"
            )
        )

        val postDao = db.postDao()

        val p1 = postDao.insertPost(
            PostEntity(
                authorId = u1,
                category = PostCategory.CLIP.key,
                gameTag = "Valorant",
                title = "¡ACE 1v5 en Ascent con 14 HP y sin balas en la primaria!",
                content = "Todavía me tiemblan las manos. El último duelo con Sheriff a través del humo fue pura lectura de mapa. En PlayForja podemos subir las jugadas tal cual pasan sin algoritmos que las entierren.",
                mediaType = "DRAWABLE_ARENA",
                hashtags = "#Valorant #Clutch #PlayForja #EsportsLATAM",
                visibility = "GLOBAL",
                likesCount = 342,
                commentsCount = 3,
                repostsCount = 48,
                createdAt = now - 18 * min
            )
        )

        val p2 = postDao.insertPost(
            PostEntity(
                authorId = u4,
                category = PostCategory.SETUP.key,
                gameTag = "Hardware & RGB",
                title = "Setup Nocturno v3.0 — Cero cables a la vista",
                content = "Por fin terminé de calibrar la iluminación sincronizada con temperatura de GPU. Pantalla curva ultrawide + teclado custom con switches lineales lubricados. ¿Qué puntuación le dan al rincón de batalla?",
                mediaType = "DRAWABLE_CYBER",
                hashtags = "#SetupGamer #BattleStation #PCMasterRace #RGB",
                visibility = "GLOBAL",
                likesCount = 289,
                commentsCount = 2,
                repostsCount = 31,
                createdAt = now - 2 * hr
            )
        )

        val p3 = postDao.insertPost(
            PostEntity(
                authorId = u5,
                category = PostCategory.DEBATE.key,
                gameTag = "Comunidad Global",
                title = "Encuesta Semanal: ¿Qué torneo abrimos este viernes en PlayForja?",
                content = "La comunidad manda. Vota por el título principal de la Copa Relámpago con premios de insignias exclusivas y marco dorado para los campeones.",
                mediaType = "NONE",
                hashtags = "#TorneoPlayForja #Comunidad #Esports",
                visibility = "GLOBAL",
                likesCount = 512,
                commentsCount = 2,
                repostsCount = 74,
                pollQuestion = "¿Cuál juego protagoniza la Copa del Viernes?",
                pollOptionsCsv = "Valorant 5v5||Rocket League 3v3||Street Fighter 6 1v1||Fortnite Zero Build",
                pollVotesCsv = "184,119,76,133",
                createdAt = now - 4 * hr
            )
        )

        val p4 = postDao.insertPost(
            PostEntity(
                authorId = u2,
                category = PostCategory.LFG.key,
                gameTag = "Helldivers 2 / Co-op",
                title = "Escuadrón Táctico para Dificultad Imposible esta noche",
                content = "Necesitamos 2 jugadores con micrófono y buen rollo para farmear muestras súper raras y limpiar el sector. No importa el nivel si tienes puntería y ganas de reírte un rato.",
                mediaType = "NONE",
                hashtags = "#LFG #Coop #Escuadron #NocheGamer",
                visibility = "GLOBAL",
                likesCount = 96,
                commentsCount = 1,
                repostsCount = 14,
                lfgSlotsTotal = 4,
                lfgSlotsFilled = 2,
                createdAt = now - 5 * hr
            )
        )

        val p5 = postDao.insertPost(
            PostEntity(
                authorId = u3,
                category = PostCategory.ARTE.key,
                gameTag = "Arte Conceptual",
                title = "Estadio Cyber-Forja — Concept art hecho para nuestro clan",
                content = "Quise capturar cómo se siente entrar a PlayForja de madrugada: neón cian, metal fundido y pura energía competitiva. Si alguien quiere banners personalizados para su perfil o clan, ¡escríbanme!",
                mediaType = "DRAWABLE_HERO",
                hashtags = "#ConceptArt #Cyberpunk #ArteGamer #ClanPixel",
                visibility = "GLOBAL",
                likesCount = 415,
                commentsCount = 1,
                repostsCount = 67,
                createdAt = now - 9 * hr
            )
        )

        val p6 = postDao.insertPost(
            PostEntity(
                authorId = u2,
                category = PostCategory.DEBATE.key,
                gameTag = "Industria Gaming",
                title = "Opinión impopular: Los juegos de 25 horas bien pulidos superan a mundos abiertos de 150h vacíos",
                content = "Prefiero mil veces una campaña intensa, rejugable y memorable que pasar 80 horas recolectando 400 iconos repetidos en un mapa gigante. ¿Ustedes de qué lado están?",
                mediaType = "NONE",
                hashtags = "#DebateGamer #DiseñoDeJuegos #OpiniónLibre",
                visibility = "GLOBAL",
                likesCount = 631,
                commentsCount = 2,
                repostsCount = 92,
                createdAt = now - 14 * hr
            )
        )

        val commentDao = db.commentDao()
        commentDao.insertComment(CommentEntity(postId = p1, authorId = u4, content = "¡Ese flick con Sheriff fue ilegal! GG capitana 🔥", likesCount = 24, createdAt = now - 12 * min))
        commentDao.insertComment(CommentEntity(postId = p1, authorId = u2, content = "Templos de acero con 14 HP, brutal lectura.", likesCount = 15, createdAt = now - 9 * min))
        commentDao.insertComment(CommentEntity(postId = p1, authorId = u3, content = "Directo al top de clips de la semana ⚡", likesCount = 11, createdAt = now - 5 * min))

        commentDao.insertComment(CommentEntity(postId = p2, authorId = u1, content = "10/10 ese manejo de cables, ¿qué brazo articulado usas para el monitor?", likesCount = 19, createdAt = now - 90 * min))
        commentDao.insertComment(CommentEntity(postId = p2, authorId = u3, content = "Los reflejos cian y naranja le dan todo el vibe PlayForja.", likesCount = 14, createdAt = now - 45 * min))

        commentDao.insertComment(CommentEntity(postId = p3, authorId = u1, content = "¡Valorant 5v5 sí o sí, nuestro escuadrón ya está calentando!", likesCount = 31, createdAt = now - 3 * hr))
        commentDao.insertComment(CommentEntity(postId = p3, authorId = u2, content = "Un poco de amor para Street Fighter 6 1v1, ¡quiero ver combos!", likesCount = 22, createdAt = now - 2 * hr))

        commentDao.insertComment(CommentEntity(postId = p4, authorId = u1, content = "Si les falta apoyo táctico a las 22:00 me sumo con lanzacohetes.", likesCount = 8, createdAt = now - 4 * hr))
        commentDao.insertComment(CommentEntity(postId = p5, authorId = u5, content = "Obra maestra Sofía, vamos a destacarlo en la vitrina de la semana.", likesCount = 42, createdAt = now - 7 * hr))
        commentDao.insertComment(CommentEntity(postId = p6, authorId = u4, content = "100% de acuerdo. Calidad sobre relleno siempre.", likesCount = 39, createdAt = now - 11 * hr))
        commentDao.insertComment(CommentEntity(postId = p6, authorId = u3, content = "Totalmente, por eso los indies siguen ganando corazones.", likesCount = 27, createdAt = now - 10 * hr))

        val clanDao = db.clanDao()
        clanDao.insertClan(
            ClanEntity(
                name = "Forjadores del Alba",
                tag = "FORJA",
                description = "Clan competitivo multijuego. Entrenamientos diarios, torneos internos y apoyo total a streamers emergentes.",
                gameFocus = "Valorant • CS2 • Apex",
                bannerPreset = "banner_forge",
                membersCount = 428,
                isJoined = false,
                leaderName = "Valkyria Nova",
                region = "LATAM / España"
            )
        )
        clanDao.insertClan(
            ClanEntity(
                name = "Caballeros del Abismo",
                tag = "SOULS",
                description = "Comunidad dedicada a Soulsborne, Elden Ring, Monster Hunter y desafíos No-Hit.",
                gameFocus = "Elden Ring • Sekiro • MH Wilds",
                bannerPreset = "banner_cyber",
                membersCount = 315,
                isJoined = false,
                leaderName = "Kuro Tanaka",
                region = "Global"
            )
        )
        clanDao.insertClan(
            ClanEntity(
                name = "Pixel & Código Libre",
                tag = "PIXEL",
                description = "Espacio para artistas, modders, desarrolladores indie y creadores de contenido sin límites.",
                gameFocus = "Indie • Mods • Pixel Art",
                bannerPreset = "banner_arena",
                membersCount = 264,
                isJoined = false,
                leaderName = "Sofía PixelArt",
                region = "Hispano / Global"
            )
        )
        clanDao.insertClan(
            ClanEntity(
                name = "Overclockers Nocturnos",
                tag = "RGBX",
                description = "Hardware extremo, setups custom, benchmarks y partidas LFG hasta el amanecer.",
                gameFocus = "Hardware • Warzone • Rocket League",
                bannerPreset = "banner_cyber",
                membersCount = 519,
                isJoined = false,
                leaderName = "Drako Montoya",
                region = "América"
            )
        )

        val chatDao = db.chatDao()
        val t1 = chatDao.insertThread(
            ChatThreadEntity(
                participantUserId = u1,
                participantName = "Valkyria Nova",
                participantHandle = "valkyria_gg",
                participantAvatarPreset = "preset_knight",
                participantFrame = FrameStyle.EMBER.key,
                participantStatus = GamerStatus.STREAMING.key,
                isClanChat = false,
                lastMessage = "¡Bienvenida a PlayForja! Cuando quieras armamos squad en Ranked 🔥",
                lastTimestamp = now - 20 * min,
                unreadCount = 1
            )
        )
        chatDao.insertMessage(
            ChatMessageEntity(
                threadId = t1,
                senderId = u1,
                senderName = "Valkyria Nova",
                content = "¡Bienvenida a PlayForja! Cuando quieras armamos squad en Ranked 🔥",
                attachmentTag = "INVITACIÓN DE SQUAD",
                timestamp = now - 20 * min
            )
        )

        val t2 = chatDao.insertThread(
            ChatThreadEntity(
                participantUserId = 0L,
                participantName = "Sala Global [FORJA]",
                participantHandle = "clan_forja",
                participantAvatarPreset = "preset_gold",
                participantFrame = FrameStyle.GOLD.key,
                participantStatus = GamerStatus.ONLINE.key,
                isClanChat = true,
                lastMessage = "Drako: ¿Quién se apunta a las customs de esta noche?",
                lastTimestamp = now - 50 * min,
                unreadCount = 2
            )
        )
        chatDao.insertMessage(
            ChatMessageEntity(
                threadId = t2,
                senderId = u5,
                senderName = "PlayForja HQ",
                content = "¡Recuerden que el torneo relámpago abre inscripciones a las 20:00!",
                timestamp = now - 2 * hr
            )
        )
        chatDao.insertMessage(
            ChatMessageEntity(
                threadId = t2,
                senderId = u4,
                senderName = "Drako Montoya",
                content = "¿Quién se apunta a las customs de esta noche?",
                timestamp = now - 50 * min
            )
        )

        val t3 = chatDao.insertThread(
            ChatThreadEntity(
                participantUserId = u3,
                participantName = "Sofía PixelArt",
                participantHandle = "sofia_pixel",
                participantAvatarPreset = "preset_plasma",
                participantFrame = FrameStyle.PLASMA.key,
                participantStatus = GamerStatus.ONLINE.key,
                isClanChat = false,
                lastMessage = "Si necesitas un banner neón para tu perfil me dices!",
                lastTimestamp = now - 3 * hr,
                unreadCount = 0
            )
        )
        chatDao.insertMessage(
            ChatMessageEntity(
                threadId = t3,
                senderId = u3,
                senderName = "Sofía PixelArt",
                content = "Si necesitas un banner neón para tu perfil me dices!",
                timestamp = now - 3 * hr
            )
        )

        val notificationDao = db.notificationDao()
        notificationDao.insertNotification(
            NotificationEntity(
                userId = 0L,
                type = "BADGE",
                actorName = "PlayForja HQ",
                message = "¡Bienvenido a PlayForja! Personaliza tu perfil, comparte lo que quieras y conquista los clanes.",
                isRead = false,
                createdAt = now - 10 * min
            )
        )
        notificationDao.insertNotification(
            NotificationEntity(
                userId = 0L,
                type = "CLAN",
                actorName = "Forjadores del Alba",
                message = "Nuevo torneo abierto en tu región: Copa Forja 5v5.",
                isRead = false,
                createdAt = now - 45 * min
            )
        )
    }
}
