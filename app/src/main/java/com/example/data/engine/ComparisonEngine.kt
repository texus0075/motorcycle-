package com.example.data.engine

import com.example.data.models.CategoryScore
import com.example.data.models.ComparisonReport
import com.example.data.models.MotorcycleEntity
import com.example.data.models.UseCasePriority
import kotlin.math.roundToInt

object ComparisonEngine {

    /**
     * Evaluates 2 or 3 motorcycles and produces a comprehensive,
     * transparent, multi-dimensional comparison report.
     */
    fun evaluate(
        bikeA: MotorcycleEntity,
        bikeB: MotorcycleEntity,
        bikeC: MotorcycleEntity? = null,
        priority: UseCasePriority = UseCasePriority.BEST_OVERALL
    ): ComparisonReport {
        val bikes = listOfNotNull(bikeA, bikeB, bikeC)

        // 1. Calculate raw category scores (0-100) for each bike
        val perfScores = bikes.map { calcPerformanceScore(it) }
        val comfortScores = bikes.map { calcComfortScore(it) }
        val featScores = bikes.map { calcFeaturesScore(it) }
        val safetyScores = bikes.map { calcSafetyScore(it) }
        val practicalityScores = bikes.map { calcPracticalityScore(it) }
        val mileageScores = bikes.map { calcMileageScore(it) }
        val valueScores = bikes.map { calcValueScore(it) }
        val techScores = bikes.map { calcTechScore(it) }
        val touringScores = bikes.map { calcTouringScore(it) }
        val cityScores = bikes.map { calcCityScore(it) }
        val beginnerScores = bikes.map { calcBeginnerScore(it) }

        // 2. Compute weighted overall score based on the chosen UseCasePriority
        val overallScores = bikes.indices.map { i ->
            computeWeightedScore(
                priority = priority,
                perf = perfScores[i],
                comfort = comfortScores[i],
                feat = featScores[i],
                safety = safetyScores[i],
                practicality = practicalityScores[i],
                mileage = mileageScores[i],
                value = valueScores[i],
                tech = techScores[i],
                touring = touringScores[i],
                city = cityScores[i],
                beginner = beginnerScores[i]
            )
        }

        val overallWinnerIdx = overallScores.indices.maxByOrNull { overallScores[it] } ?: 0
        val perfWinnerIdx = perfScores.indices.maxByOrNull { perfScores[it] } ?: 0
        val mileageWinnerIdx = mileageScores.indices.maxByOrNull { mileageScores[it] } ?: 0
        val valueWinnerIdx = valueScores.indices.maxByOrNull { valueScores[it] } ?: 0
        val safetyWinnerIdx = safetyScores.indices.maxByOrNull { safetyScores[it] } ?: 0
        val cityWinnerIdx = cityScores.indices.maxByOrNull { cityScores[it] } ?: 0
        val touringWinnerIdx = touringScores.indices.maxByOrNull { touringScores[it] } ?: 0
        val beginnerWinnerIdx = beginnerScores.indices.maxByOrNull { beginnerScores[it] } ?: 0

        // 3. Assemble category score rows
        val categoryScores = listOf(
            createCategoryScore("Performance & Power", perfScores),
            createCategoryScore("Fuel Efficiency & Range", mileageScores),
            createCategoryScore("Value for Money", valueScores),
            createCategoryScore("Electronics & Tech", techScores),
            createCategoryScore("Safety & Braking", safetyScores),
            createCategoryScore("City Usability & Agility", cityScores),
            createCategoryScore("Touring & Highway Comfort", touringScores),
            createCategoryScore("Beginner Friendliness", beginnerScores),
            createCategoryScore("Ergonomics & Practicality", practicalityScores)
        )

        // 4. Extract key spec-backed advantages for each motorcycle
        val advantagesA = generateAdvantages(bikeA, listOfNotNull(bikeB, bikeC))
        val advantagesB = generateAdvantages(bikeB, listOfNotNull(bikeA, bikeC))
        val advantagesC = bikeC?.let { generateAdvantages(it, listOf(bikeA, bikeB)) } ?: emptyList()

        // 5. Generate transparent natural language verdict
        val winnerBike = bikes[overallWinnerIdx]
        val verdict = generateWrittenVerdict(
            winner = winnerBike,
            allBikes = bikes,
            winnerScore = overallScores[overallWinnerIdx],
            priority = priority,
            perfWinner = bikes[perfWinnerIdx],
            mileageWinner = bikes[mileageWinnerIdx],
            valueWinner = bikes[valueWinnerIdx]
        )

        return ComparisonReport(
            bikeA = bikeA,
            bikeB = bikeB,
            bikeC = bikeC,
            priority = priority,
            overallScoreA = overallScores[0],
            overallScoreB = overallScores[1],
            overallScoreC = overallScores.getOrNull(2),
            overallWinnerIndex = overallWinnerIdx,
            performanceWinnerIndex = perfWinnerIdx,
            mileageWinnerIndex = mileageWinnerIdx,
            valueWinnerIndex = valueWinnerIdx,
            safetyWinnerIndex = safetyWinnerIdx,
            cityWinnerIndex = cityWinnerIdx,
            touringWinnerIndex = touringWinnerIdx,
            beginnerWinnerIndex = beginnerWinnerIdx,
            categoryScores = categoryScores,
            writtenVerdict = verdict,
            keyAdvantagesA = advantagesA,
            keyAdvantagesB = advantagesB,
            keyAdvantagesC = advantagesC
        )
    }

    private fun createCategoryScore(name: String, scores: List<Int>): CategoryScore {
        val leader = scores.indices.maxByOrNull { scores[it] } ?: 0
        return CategoryScore(
            categoryName = name,
            scoreA = scores[0],
            scoreB = scores[1],
            scoreC = scores.getOrNull(2),
            leaderIndex = leader
        )
    }

    private fun calcPerformanceScore(bike: MotorcycleEntity): Int {
        val s = bike.specs
        // Power (0-200 HP normalized to 0-45 points)
        val powerPts = (s.maxPowerHp / 200.0 * 45).coerceIn(0.0, 45.0)
        // Torque (0-120 Nm normalized to 0-25 points)
        val torquePts = (s.maxTorqueNm / 120.0 * 25).coerceIn(0.0, 25.0)
        // 0-100 acceleration (2.5s is 20 pts, 12s is 2 pts)
        val accelPts = if (s.accel0To100Sec > 0) {
            ((12.0 - s.accel0To100Sec) / 9.5 * 20.0).coerceIn(2.0, 20.0)
        } else 10.0
        // Top speed bonus
        val speedPts = (s.topSpeedKmh / 300.0 * 10.0).coerceIn(0.0, 10.0)
        return (powerPts + torquePts + accelPts + speedPts).roundToInt().coerceIn(30, 99)
    }

    private fun calcComfortScore(bike: MotorcycleEntity): Int {
        val s = bike.specs
        // Ergonomics & seat height (780mm to 820mm is comfortable, >840mm is aggressive)
        val seatPts = when {
            s.seatHeightMm in 780..820 -> 35
            s.seatHeightMm < 780 -> 32
            else -> 24
        }
        // Suspension travel (more travel = softer ride on bumps)
        val travelPts = ((s.frontTravelMm + s.rearTravelMm) / 400.0 * 30).coerceIn(10.0, 30.0).toInt()
        // Riding posture by category
        val posturePts = when (bike.category) {
            "Adventure / ADV", "Touring", "Cruiser" -> 35
            "Naked", "Street", "Retro / Classic" -> 28
            else -> 18 // Sport / Super Sport aggressive clip-ons
        }
        return (seatPts + travelPts + posturePts).coerceIn(35, 98)
    }

    private fun calcFeaturesScore(bike: MotorcycleEntity): Int {
        val s = bike.specs
        var score = 30
        if (s.hasTftDisplay) score += 15
        if (s.hasBluetooth) score += 10
        if (s.hasNavigation) score += 10
        if (s.quickShifter.contains("Up", ignoreCase = true) || s.quickShifter.contains("Bidi", ignoreCase = true)) score += 15
        if (s.hasCruiseControl) score += 10
        if (s.hasRideByWire) score += 10
        return score.coerceIn(30, 99)
    }

    private fun calcSafetyScore(bike: MotorcycleEntity): Int {
        val s = bike.specs
        var score = 40
        if (s.absSystem.contains("Dual", ignoreCase = true)) score += 15
        if (s.hasCorneringAbs) score += 20
        if (s.hasTractionControl) score += 15
        if (s.hasSlipperClutch) score += 10
        return score.coerceIn(40, 99)
    }

    private fun calcPracticalityScore(bike: MotorcycleEntity): Int {
        val s = bike.specs
        // Ground clearance
        val gcPts = (s.groundClearanceMm / 230.0 * 30).coerceIn(10.0, 30.0).toInt()
        // Kerb weight (lighter = easier parking & slow manoeuvres)
        val weightPts = ((240.0 - s.kerbWeightKg) / 100.0 * 35).coerceIn(5.0, 35.0).toInt()
        // Mileage practicality
        val mileagePts = (s.mileageKmpl / 50.0 * 35).coerceIn(5.0, 35.0).toInt()
        return (gcPts + weightPts + mileagePts).coerceIn(30, 98)
    }

    private fun calcMileageScore(bike: MotorcycleEntity): Int {
        val s = bike.specs
        val kmplPts = (s.mileageKmpl / 55.0 * 70).coerceIn(15.0, 70.0)
        val tankRangePts = ((s.fuelTankCapacityL * s.mileageKmpl) / 500.0 * 30).coerceIn(10.0, 30.0)
        return (kmplPts + tankRangePts).roundToInt().coerceIn(30, 99)
    }

    private fun calcValueScore(bike: MotorcycleEntity): Int {
        val s = bike.specs
        // Price to power ratio
        val hpPerThousandDollars = if (bike.basePrice > 0) (s.maxPowerHp / (bike.basePrice / 1000.0)) else 10.0
        val ratioPts = (hpPerThousandDollars * 7.5).coerceIn(20.0, 55.0)
        val lowPriceBonus = if (bike.basePrice <= 4000) 40.0 else if (bike.basePrice <= 10000) 25.0 else 10.0
        return (ratioPts + lowPriceBonus).roundToInt().coerceIn(35, 97)
    }

    private fun calcTechScore(bike: MotorcycleEntity): Int {
        val s = bike.specs
        var score = 35
        if (s.hasTftDisplay) score += 15
        if (s.hasRideByWire) score += 15
        if (s.hasCorneringAbs) score += 15
        if (s.hasTractionControl) score += 10
        if (s.hasBluetooth) score += 10
        return score.coerceIn(35, 99)
    }

    private fun calcTouringScore(bike: MotorcycleEntity): Int {
        val s = bike.specs
        val tankPts = (s.fuelTankCapacityL / 20.0 * 35).coerceIn(10.0, 35.0).toInt()
        val seatComfort = calcComfortScore(bike) * 0.4
        val highwayPower = (s.maxPowerHp / 100.0 * 25).coerceIn(5.0, 25.0).toInt()
        return (tankPts + seatComfort + highwayPower).toInt().coerceIn(30, 98)
    }

    private fun calcCityScore(bike: MotorcycleEntity): Int {
        val s = bike.specs
        val agility = ((210.0 - s.kerbWeightKg) / 80.0 * 40).coerceIn(10.0, 40.0).toInt()
        val lowSeat = if (s.seatHeightMm <= 810) 30 else 18
        val economy = (s.mileageKmpl / 50.0 * 30).coerceIn(10.0, 30.0).toInt()
        return (agility + lowSeat + economy).coerceIn(35, 99)
    }

    private fun calcBeginnerScore(bike: MotorcycleEntity): Int {
        val s = bike.specs
        // Friendly power (between 15 and 45 HP is ideal for learning)
        val powerFriendliness = when {
            s.maxPowerHp <= 25 -> 35
            s.maxPowerHp <= 48 -> 30
            s.maxPowerHp <= 80 -> 18
            else -> 8
        }
        val seatAccessibility = if (s.seatHeightMm <= 810) 30 else 18
        val manageableWeight = if (s.kerbWeightKg <= 165) 25 else 12
        val safetyBuffer = if (s.absSystem.contains("Dual", ignoreCase = true) || s.hasTractionControl) 10 else 5
        return (powerFriendliness + seatAccessibility + manageableWeight + safetyBuffer).coerceIn(30, 98)
    }

    private fun computeWeightedScore(
        priority: UseCasePriority,
        perf: Int,
        comfort: Int,
        feat: Int,
        safety: Int,
        practicality: Int,
        mileage: Int,
        value: Int,
        tech: Int,
        touring: Int,
        city: Int,
        beginner: Int
    ): Int {
        return when (priority) {
            UseCasePriority.BEST_OVERALL -> {
                (perf * 0.20 + value * 0.15 + safety * 0.15 + mileage * 0.15 + feat * 0.15 + city * 0.10 + comfort * 0.10).roundToInt()
            }
            UseCasePriority.PERFORMANCE -> {
                (perf * 0.45 + tech * 0.20 + safety * 0.15 + feat * 0.10 + value * 0.10).roundToInt()
            }
            UseCasePriority.DAILY_COMMUTE -> {
                (city * 0.35 + mileage * 0.30 + practicality * 0.20 + comfort * 0.15).roundToInt()
            }
            UseCasePriority.MILEAGE -> {
                (mileage * 0.50 + value * 0.25 + city * 0.15 + practicality * 0.10).roundToInt()
            }
            UseCasePriority.TOURING -> {
                (touring * 0.40 + comfort * 0.25 + perf * 0.15 + safety * 0.10 + tech * 0.10).roundToInt()
            }
            UseCasePriority.BEGINNER -> {
                (beginner * 0.45 + safety * 0.20 + city * 0.15 + value * 0.10 + mileage * 0.10).roundToInt()
            }
            UseCasePriority.BUDGET -> {
                (value * 0.45 + mileage * 0.25 + practicality * 0.15 + safety * 0.15).roundToInt()
            }
            UseCasePriority.COMFORT -> {
                (comfort * 0.45 + touring * 0.25 + practicality * 0.15 + safety * 0.15).roundToInt()
            }
            UseCasePriority.FEATURES_TECH -> {
                (tech * 0.40 + feat * 0.30 + safety * 0.15 + perf * 0.15).roundToInt()
            }
            UseCasePriority.SPORT_RIDING -> {
                (perf * 0.40 + tech * 0.25 + safety * 0.20 + feat * 0.15).roundToInt()
            }
        }.coerceIn(40, 99)
    }

    private fun generateAdvantages(target: MotorcycleEntity, rivals: List<MotorcycleEntity>): List<String> {
        val advantages = mutableListOf<String>()
        val ts = target.specs

        // Power advantage
        if (rivals.all { ts.maxPowerHp > it.specs.maxPowerHp }) {
            advantages.add("Leading Power: ${ts.maxPowerHp} HP puts it ahead of rivals in high-speed acceleration")
        }
        // Torque advantage
        if (rivals.all { ts.maxTorqueNm > it.specs.maxTorqueNm }) {
            advantages.add("Top Torque: ${ts.maxTorqueNm} Nm ensures stronger pulling grunt across the rev range")
        }
        // Light weight advantage
        if (rivals.all { ts.kerbWeightKg < it.specs.kerbWeightKg }) {
            advantages.add("Lightest Kerb Weight: At ${ts.kerbWeightKg} kg, it offers superior flickability and effortless slow-speed handling")
        }
        // Fuel economy advantage
        if (rivals.all { ts.mileageKmpl > it.specs.mileageKmpl }) {
            advantages.add("Highest Mileage: ${ts.mileageKmpl} km/l delivers unmatched fuel efficiency and lowest running cost")
        }
        // Seat height accessibility
        if (rivals.all { ts.seatHeightMm < it.specs.seatHeightMm }) {
            advantages.add("Most Accessible Seat Height: ${ts.seatHeightMm} mm inspires confidence for riders of all statures")
        }
        // Electronics advantage
        if (ts.hasCorneringAbs && rivals.any { !it.specs.hasCorneringAbs }) {
            advantages.add("Advanced Lean-Sensitive Safety: Equipped with Cornering ABS")
        }
        if (ts.hasTftDisplay && rivals.any { !it.specs.hasTftDisplay }) {
            advantages.add("Full Color TFT Cockpit with advanced telemetry")
        }
        if (ts.quickShifter.contains("Up", ignoreCase = true) && rivals.any { it.specs.quickShifter.contains("None", ignoreCase = true) }) {
            advantages.add("Quickshifter equipped for seamless clutchless gear transitions")
        }
        // Fuel tank capacity advantage
        if (rivals.all { ts.fuelTankCapacityL > it.specs.fuelTankCapacityL }) {
            advantages.add("Largest Fuel Tank: ${ts.fuelTankCapacityL}L capacity minimizes refueling stops on long hauls")
        }

        if (advantages.isEmpty()) {
            advantages.add("Balanced all-rounder with strong real-world reliability and ergonomics")
        }
        return advantages.take(4)
    }

    private fun generateWrittenVerdict(
        winner: MotorcycleEntity,
        allBikes: List<MotorcycleEntity>,
        winnerScore: Int,
        priority: UseCasePriority,
        perfWinner: MotorcycleEntity,
        mileageWinner: MotorcycleEntity,
        valueWinner: MotorcycleEntity
    ): String {
        val s = winner.specs
        val priorityText = when (priority) {
            UseCasePriority.BEST_OVERALL -> "balanced overall package"
            UseCasePriority.PERFORMANCE -> "pure performance and racetrack agility"
            UseCasePriority.DAILY_COMMUTE -> "daily commuting, agile traffic cutting, and running economy"
            UseCasePriority.MILEAGE -> "maximum fuel efficiency and low operating expenses"
            UseCasePriority.TOURING -> "long-distance touring comfort and highway stability"
            UseCasePriority.BEGINNER -> "rider confidence, manageable ergonomics, and accessible power"
            UseCasePriority.BUDGET -> "bang-for-the-buck and equipment density per dollar"
            UseCasePriority.COMFORT -> "rider ergonomics and suspension plushness"
            UseCasePriority.FEATURES_TECH -> "cutting-edge electronics and cockpit connectivity"
            UseCasePriority.SPORT_RIDING -> "aggressive riding dynamics and high-speed cornering"
        }

        val perfDetail = if (winner.id == perfWinner.id) {
            "It dominates the performance benchmarks with ${s.maxPowerHp} HP and an estimated 0-100 km/h sprint of ${s.accel0To100Sec}s."
        } else {
            "While ${perfWinner.modelName} edges it out in raw output (${perfWinner.specs.maxPowerHp} HP), ${winner.modelName} compensates with a much more versatile chassis and daily rideability."
        }

        val economyDetail = if (winner.id == mileageWinner.id) {
            "It also proves to be the thriftiest at the pump with an exceptional ${s.mileageKmpl} km/l."
        } else {
            "Its certified ${s.mileageKmpl} km/l fuel economy strikes a sweet spot against its rivals."
        }

        return "FINAL VERDICT: For riders prioritizing $priorityText, the ${winner.brandId.uppercase()} ${winner.modelName} takes the checkered flag with a score of $winnerScore/100. $perfDetail $economyDetail Furthermore, with a kerb weight of ${s.kerbWeightKg} kg and ${s.absSystem}, it delivers high rider confidence without compromise."
    }
}
