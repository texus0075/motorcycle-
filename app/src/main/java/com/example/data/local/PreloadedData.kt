package com.example.data.local

import com.example.data.local.dataset.EuropeanBikes
import com.example.data.local.dataset.HeritageAndAsianBikes
import com.example.data.local.dataset.JapaneseBikes
import com.example.data.models.BrandEntity
import com.example.data.models.MotorcycleEntity
import com.example.data.models.VariantEntity

object PreloadedData {

    val brands = listOf(
        BrandEntity(
            id = "yamaha",
            name = "Yamaha",
            country = "Japan",
            foundedYear = 1955,
            history = "Founded in Iwata, Shizuoka, Yamaha Motor Co. is renowned worldwide for its race-bred YZF sportbikes, MT hyper nakets, Crossplane engine philosophy, and Dakar-proven rally machines.",
            websiteUrl = "https://www.yamahamotorsports.com",
            logoUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFF0038A8
        ),
        BrandEntity(
            id = "ktm",
            name = "KTM",
            country = "Austria",
            foundedYear = 1934,
            history = "KTM Sportmotorcycle GmbH is an Austrian manufacturer famed for its 'Ready to Race' mantra, dominating Dakar rallies, Moto3/Moto2/MotoGP, and engineering radical lightweight trellis-frame machines.",
            websiteUrl = "https://www.ktm.com",
            logoUrl = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFFFF6600
        ),
        BrandEntity(
            id = "kawasaki",
            name = "Kawasaki",
            country = "Japan",
            foundedYear = 1896,
            history = "Kawasaki Heavy Industries produces the legendary Ninja family, supercharged H2 hyperbikes, and celebrated middleweights engineered for peerless high-RPM durability and WSBK supremacy.",
            websiteUrl = "https://www.kawasaki.com",
            logoUrl = "https://images.unsplash.com/photo-1591637333184-19aa84b3e01f?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFF49C300
        ),
        BrandEntity(
            id = "ducati",
            name = "Ducati",
            country = "Italy",
            foundedYear = 1926,
            history = "Based in Borgo Panigale, Bologna, Ducati represents pure Italian passion, desmodromic valve actuation, V4 engines, and multiple MotoGP & World Superbike Championship crowns.",
            websiteUrl = "https://www.ducati.com",
            logoUrl = "https://images.unsplash.com/photo-1616455579100-2ceaa4eb2d37?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFFCC0000
        ),
        BrandEntity(
            id = "bmw",
            name = "BMW Motorrad",
            country = "Germany",
            foundedYear = 1923,
            history = "BMW Motorrad pioneered the iconic Boxer twin engine, Telelever suspension, the world-conquering R 1250/1300 GS adventure motorcycle, and the ShiftCam powered S 1000 RR superbike.",
            websiteUrl = "https://www.bmw-motorrad.com",
            logoUrl = "https://images.unsplash.com/photo-1599819811279-d5ad9cccf838?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFF0066B1
        ),
        BrandEntity(
            id = "honda",
            name = "Honda",
            country = "Japan",
            foundedYear = 1948,
            history = "The world's largest motorcycle manufacturer, Honda is revered for bulletproof reliability, Fireblade engineering, DCT automatic transmissions, and iconic Africa Twin adventures.",
            websiteUrl = "https://powersports.honda.com",
            logoUrl = "https://images.unsplash.com/photo-1609630875171-b1321377ee65?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFFE4002B
        ),
        BrandEntity(
            id = "triumph",
            name = "Triumph",
            country = "United Kingdom",
            foundedYear = 1902,
            history = "Hinckley's finest, Triumph Motorcycles is celebrated for its distinctive triple-cylinder engines powering Moto2, modern classics like the Bonneville, and class-defining Street Triple nakeds.",
            websiteUrl = "https://www.triumphmotorcycles.com",
            logoUrl = "https://images.unsplash.com/photo-1558981420-87aa9dad1c89?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFF0B1F3F
        ),
        BrandEntity(
            id = "royalenfield",
            name = "Royal Enfield",
            country = "India / UK",
            foundedYear = 1901,
            history = "The oldest global motorcycle brand in continuous production. Known for soulful single & twin cylinder modern classics like the Classic 350, Continental GT 650, and the Sherpa 450 Himalayan.",
            websiteUrl = "https://www.royalenfield.com",
            logoUrl = "https://images.unsplash.com/photo-1609630875289-5326620577be?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFFD4AF37
        ),
        BrandEntity(
            id = "aprilia",
            name = "Aprilia",
            country = "Italy",
            foundedYear = 1945,
            history = "Part of the Piaggio Group, Aprilia is an engineering juggernaut holding 54 world titles, celebrated for twin-spar aluminium chassis design, V4 engines, and razor-sharp sport models.",
            websiteUrl = "https://www.aprilia.com",
            logoUrl = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFFD62226
        ),
        BrandEntity(
            id = "harleydavidson",
            name = "Harley-Davidson",
            country = "United States",
            foundedYear = 1903,
            history = "An enduring American icon founded in Milwaukee, Wisconsin, renowned for air-cooled & liquid-cooled V-Twin cruisers, grand American touring models, and the Revolution Max adventure platform.",
            websiteUrl = "https://www.harley-davidson.com",
            logoUrl = "https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFFFF6600
        ),
        BrandEntity(
            id = "suzuki",
            name = "Suzuki",
            country = "Japan",
            foundedYear = 1909,
            history = "Famous for high-speed aerodynamics with the Hayabusa, legendary GSX-R track dominance, and rugged V-Strom adventure dual-sport tourers.",
            websiteUrl = "https://suzukicycles.com",
            logoUrl = "https://images.unsplash.com/photo-1591637333184-19aa84b3e01f?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFF003087
        ),
        BrandEntity(
            id = "tvs",
            name = "TVS Motor",
            country = "India",
            foundedYear = 1978,
            history = "TVS Racing is India's pioneer motorsport factory, co-engineering platforms with BMW Motorrad and producing the high-tech Apache RR/RTR 310 series and modern Ronin cruisers.",
            websiteUrl = "https://www.tvsmotor.com",
            logoUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFF0B2F87
        ),
        BrandEntity(
            id = "cfmoto",
            name = "CFMOTO",
            country = "China",
            foundedYear = 1989,
            history = "Rapidly expanding global power known for premium Kiska-designed sport, naked and adventure bikes with high-spec Brembo, Bosch, and KYB components.",
            websiteUrl = "https://www.cfmoto.com",
            logoUrl = "https://images.unsplash.com/photo-1616455579100-2ceaa4eb2d37?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFF00A3E0
        ),
        BrandEntity(
            id = "bajaj",
            name = "Bajaj Auto",
            country = "India",
            foundedYear = 1945,
            history = "Pioneering Indian two-wheeler giant and global manufacturing powerhouse, creator of the legendary Pulsar generation and Dominar hyper-tourers.",
            websiteUrl = "https://www.bajajauto.com",
            logoUrl = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFF005696
        ),
        BrandEntity(
            id = "hero",
            name = "Hero MotoCorp",
            country = "India",
            foundedYear = 1984,
            history = "The world's largest two-wheeler manufacturer by volume, renowned for bulletproof reliability, modern Karizma sport platforms, and Dakar-tested XPulse rally machines.",
            websiteUrl = "https://www.heromotocorp.com",
            logoUrl = "https://images.unsplash.com/photo-1609630875171-b1321377ee65?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFFEE1C25
        ),
        BrandEntity(
            id = "husqvarna",
            name = "Husqvarna",
            country = "Sweden / Austria",
            foundedYear = 1903,
            history = "Renowned Swedish heritage marque celebrating minimalist Scandinavian design, lightweight agility, and cutting-edge Svartpilen and Vitpilen modern classics.",
            websiteUrl = "https://www.husqvarna-motorcycles.com",
            logoUrl = "https://images.unsplash.com/photo-1558981420-87aa9dad1c89?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFF003057
        ),
        BrandEntity(
            id = "mvagusta",
            name = "MV Agusta",
            country = "Italy",
            foundedYear = 1945,
            history = "'Motorcycle Art' sculpted in Schiranna on Lake Varese, holding 38 World Championship titles and famed for high-revving triples and limited-edition superbikes.",
            websiteUrl = "https://www.mvagusta.com",
            logoUrl = "https://images.unsplash.com/photo-1616455579100-2ceaa4eb2d37?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFF990000
        ),
        BrandEntity(
            id = "indian",
            name = "Indian Motorcycle",
            country = "United States",
            foundedYear = 1901,
            history = "America's first motorcycle company, legendary for Scout bobbers, powerful liquid-cooled V-twins, and iconic heritage styling from Spirit Lake, Iowa.",
            websiteUrl = "https://www.indianmotorcycle.com",
            logoUrl = "https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFF8B0000
        ),
        BrandEntity(
            id = "benelli",
            name = "Benelli",
            country = "Italy",
            foundedYear = 1911,
            history = "Established in Pesaro, Italy, Benelli is famous for evocative exhaust notes, rugged TRK adventure tourers, and stylish Leoncino scramblers.",
            websiteUrl = "https://www.benelli.com",
            logoUrl = "https://images.unsplash.com/photo-1599819811279-d5ad9cccf838?auto=format&fit=crop&w=400&q=80",
            accentColorHex = 0xFF006837
        )
    )

    // Combined catalog of 30+ top global motorcycles
    val motorcycles: List<MotorcycleEntity> =
        JapaneseBikes.list + EuropeanBikes.list + HeritageAndAsianBikes.list

    val variants = listOf(
        // Yamaha R15 V4 Variants
        VariantEntity(
            id = "yamaha-r15-v4-std",
            motorcycleId = "yamaha-r15-v4",
            variantName = "Standard Metallic Red",
            price = 2200.0,
            priceDisplay = "$2,200 (₹1,82,000)",
            highlightedFeatures = "Dual Channel ABS, Traction Control, Assist & Slipper Clutch",
            variantImageUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80",
            colorName = "Metallic Red",
            colorHex = "#B91C1C"
        ),
        VariantEntity(
            id = "yamaha-r15-v4-racing-blue",
            motorcycleId = "yamaha-r15-v4",
            variantName = "Racing Blue Quickshifter",
            price = 2260.0,
            priceDisplay = "$2,260 (₹1,87,000)",
            highlightedFeatures = "Factory Quick Shifter Included, Blue Alloy Wheels, Y-Connect App",
            variantImageUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80",
            colorName = "Racing Blue",
            colorHex = "#0038A8"
        ),
        VariantEntity(
            id = "yamaha-r15m",
            motorcycleId = "yamaha-r15-v4",
            variantName = "R15M Silver Carbon Edition",
            price = 2380.0,
            priceDisplay = "$2,380 (₹1,96,500)",
            highlightedFeatures = "Metallic Grey Livery, 3D Emblem, Golden Brake Calipers, Dedicated TFT Screen Theme",
            variantImageUrl = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
            colorName = "Carbon Silver",
            colorHex = "#64748B"
        ),

        // KTM RC 200 Variants
        VariantEntity(
            id = "ktm-rc-200-std",
            motorcycleId = "ktm-rc-200",
            variantName = "Standard Electronic Orange",
            price = 2650.0,
            priceDisplay = "$2,650 (₹2,18,000)",
            highlightedFeatures = "WP APEX 43mm Big Piston Fork, Supermoto ABS, 320mm Front Disc",
            variantImageUrl = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
            colorName = "Electronic Orange",
            colorHex = "#FF6600"
        ),
        VariantEntity(
            id = "ktm-rc-200-gp",
            motorcycleId = "ktm-rc-200",
            variantName = "MotoGP Factory Racing Edition",
            price = 2710.0,
            priceDisplay = "$2,710 (₹2,23,000)",
            highlightedFeatures = "Factory KTM Factory Racing Decals, Orange Powder-Coated Wheels, Tinted Windshield",
            variantImageUrl = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
            colorName = "Factory Racing Black & Orange",
            colorHex = "#1E293B"
        ),

        // Kawasaki Ninja 300 Variants
        VariantEntity(
            id = "ninja-300-lime",
            motorcycleId = "kawasaki-ninja-300",
            variantName = "Lime Green KRT Edition",
            price = 4150.0,
            priceDisplay = "$4,150 (₹3,43,000)",
            highlightedFeatures = "Parallel Twin DOHC, Assist & Slipper Clutch, Petal Disc Brakes",
            variantImageUrl = "https://images.unsplash.com/photo-1591637333184-19aa84b3e01f?auto=format&fit=crop&w=800&q=80",
            colorName = "Lime Green",
            colorHex = "#49C300"
        ),
        VariantEntity(
            id = "ninja-300-gray",
            motorcycleId = "kawasaki-ninja-300",
            variantName = "Metallic Moondust Gray",
            price = 4150.0,
            priceDisplay = "$4,150 (₹3,43,000)",
            highlightedFeatures = "Sleek Stealth Gray Finish, Dual Channel ABS, 17-litre Touring Fuel Tank",
            variantImageUrl = "https://images.unsplash.com/photo-1591637333184-19aa84b3e01f?auto=format&fit=crop&w=800&q=80",
            colorName = "Moondust Gray",
            colorHex = "#475569"
        ),

        // KTM 390 Duke Variants
        VariantEntity(
            id = "ktm-390-duke-orange",
            motorcycleId = "ktm-390-duke",
            variantName = "Electronic Orange Flagship",
            price = 3750.0,
            priceDisplay = "$3,750 (₹3,11,000)",
            highlightedFeatures = "Full Adjustable WP APEX Suspension, Quickshifter+, Launch Control, Cornering ABS",
            variantImageUrl = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
            colorName = "Electronic Orange",
            colorHex = "#FF6600"
        ),
        VariantEntity(
            id = "ktm-390-duke-blue",
            motorcycleId = "ktm-390-duke",
            variantName = "Atlantic Blue Edition",
            price = 3750.0,
            priceDisplay = "$3,750 (₹3,11,000)",
            highlightedFeatures = "Metallic Blue Paint Scheme, 5-inch Glass Bonded TFT, Track Riding Mode",
            variantImageUrl = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
            colorName = "Atlantic Blue",
            colorHex = "#1D4ED8"
        )
    )

    val categories = listOf(
        "Sport",
        "Super Sport",
        "Naked",
        "Street",
        "Adventure / ADV",
        "Cruiser",
        "Touring",
        "Dual Sport",
        "Dirt / Off-road",
        "Retro / Classic",
        "Cafe Racer",
        "Scrambler",
        "Scooter",
        "Electric"
    )
}
