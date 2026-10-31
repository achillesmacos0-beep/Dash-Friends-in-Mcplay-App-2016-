package com.example.data

import com.example.R

object ToyRepository {
    val allToys: List<Toy> = listOf(
        Toy(
            id = "dash_launcher_blocks",
            number = 1,
            name = "Dash Launcher Blocks",
            character = "Dash",
            imageRes = R.drawable.toy_dash_launcher_blocks,
            tagline = "Ready, aim, launch into action!",
            description = "Features a purple Dash launcher block paired with vibrant orange target blocks. Load the projectile block and launch it to topple block towers!",
            actionName = "Launch Purple Block",
            actionDescription = "Spring-loaded block firing mechanism that knocks down stacked targets.",
            funFact = "Dash's launcher blocks can be stacked in over 15 different tower combinations!",
            barcodePattern = "DASH-LAUNCH-01",
            associatedGame = GameType.LAUNCHER_KNOCKDOWN
        ),
        Toy(
            id = "dash_boomerang",
            number = 2,
            name = "Dash Boomerang",
            character = "Dash",
            imageRes = R.drawable.toy_dash_boomerang,
            tagline = "Throw it far and watch it fly right back!",
            description = "A specially designed curved aerodynamic boomerang featuring Dash's smiling face in the center. Loops effortlessly through the air in backyard play.",
            actionName = "Throw & Curve Spin",
            actionDescription = "High-spin flight arc that loops around and returns to the thrower.",
            funFact = "Dash's boomerang design was tested in kid aerodynamic wind tunnels for smooth hovering returns!",
            barcodePattern = "DASH-BOOM-02",
            associatedGame = GameType.BOOMERANG_CATCH
        ),
        Toy(
            id = "secret_agent_dash",
            number = 3,
            name = "Secret Agent Dash",
            character = "Dash",
            imageRes = R.drawable.toy_dot_water_squirter,
            tagline = "Top-secret detective gadget for mystery missions!",
            description = "Dash disguised as a master secret agent, holding an undercover spy magnifying gadget to discover hidden Happy Meal clues and unlock secret codes.",
            actionName = "Activate Detective Lens",
            actionDescription = "Inspect hidden UV invisible ink clues and secret agent messages.",
            funFact = "Secret Agent Dash's code name in the Dash & Friends Agency is 'Agent Double-D'!",
            barcodePattern = "AGENT-DASH-03",
            associatedGame = GameType.LAUNCHER_KNOCKDOWN
        ),
        Toy(
            id = "dees_helmet",
            number = 4,
            name = "Dee's Helmet",
            character = "Dee",
            imageRes = R.drawable.toy_dees_helmet,
            tagline = "Futuristic cyber racing helmet with flip-down visor!",
            description = "Dee's iconic white and blue racing helmet with a dark interactive visor that flips down to reveal a futuristic heads-up display.",
            actionName = "Toggle Cyber Visor HUD",
            actionDescription = "Flip visor down to activate blue neon sci-fi targeting overlay.",
            funFact = "Dee wears his lucky striped helmet for super-sonic go-kart races and VR exploration!",
            barcodePattern = "DEE-HELMET-04",
            associatedGame = GameType.HELMET_RUNNER
        ),
        Toy(
            id = "del_pan",
            number = 5,
            name = "Del Pan",
            character = "Del",
            imageRes = R.drawable.toy_secret_agent_dash,
            tagline = "Sizzle, toss, and catch hot pancakes!",
            description = "Del is ready in the kitchen with a sizzling skillet pan! Flip golden brown pancakes and see how high you can stack breakfast treats.",
            actionName = "Flip Sizzling Pancake",
            actionDescription = "Toss the pancake high in the air and catch it right on the griddle.",
            funFact = "Del holds the McDonald's record for flipping 12 mini-pancakes into a stack without dropping one!",
            barcodePattern = "DEL-PAN-05",
            associatedGame = GameType.PANCAKE_FLIP
        ),
        Toy(
            id = "dot_water_squirter",
            number = 6,
            name = "Dot Water Squirter",
            character = "Dot",
            imageRes = R.drawable.toy_del_pan,
            tagline = "Make a giant splash with Dot!",
            description = "Fill Dot's water chamber and press the trigger to launch a refreshing stream of water. Ideal for hot sunny days and outdoor splash battles.",
            actionName = "Squirt Water Stream",
            actionDescription = "Twin-nozzle water squirt blast with realistic splash droplets.",
            funFact = "Dot's water squirter holds up to 3 fluid ounces of water for extended backyard play!",
            barcodePattern = "DOT-SQUIRT-06",
            associatedGame = GameType.WATER_SPLASH
        ),
        Toy(
            id = "dash_mask",
            number = 7,
            name = "Dash Mask",
            character = "Dash",
            imageRes = R.drawable.toy_dash_mask,
            tagline = "Put on the mask and become Dash!",
            description = "A lightweight green wearable face mask with Dash's smiling round eyes and black hairstyle. Features adjustable side bands for instant costume play.",
            actionName = "Try On Dash Mask",
            actionDescription = "Simulate wearing Dash's mask with photo camera frame cutouts.",
            funFact = "The Dash mask was the #1 costume accessory given out at McDonald's Dash & Friends events in 2016!",
            barcodePattern = "DASH-MASK-07",
            associatedGame = GameType.BOOMERANG_CATCH
        ),
        Toy(
            id = "dot_talking_toy",
            number = 8,
            name = "Dot Talking Toy",
            character = "Dot",
            imageRes = R.drawable.toy_dot_talking_toy,
            tagline = "Push Dot's button to hear her talk, giggle, and cheer!",
            description = "An interactive electronic talking figurine of Dot wearing her signature pink striped shirt. Tap to hear funny catchphrases, cheerful giggles, and cheerful songs.",
            actionName = "Press Talking Button",
            actionDescription = "Plays authentic cheerful voice clips, giggles, and upbeat speech.",
            funFact = "Dot's talking toy features 8 different catchphrases including 'Ready for adventure!' and 'High five!'",
            barcodePattern = "DOT-TALK-08",
            associatedGame = GameType.DOT_SOUNDBOARD
        )
    )

    fun getToyById(id: String): Toy? = allToys.find { it.id == id }
}
