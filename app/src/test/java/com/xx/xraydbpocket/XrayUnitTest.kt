package com.xx.xraydbpocket

import org.junit.Assert.assertEquals
import org.junit.Test

class XrayUnitTest {

    @Test
    fun testFormatEnergy() {
        assertEquals("530.0 eV", formatEnergy(530.0))
        assertEquals("8.048 keV", formatEnergy(8047.8))
        assertEquals("1.254 keV", formatEnergy(1253.6))
    }

    @Test
    fun testFormatConfiguration() {
        assertEquals("[Ar] 3d⁶ 4s²", formatConfiguration("[Ar] 3d6 4s2"))
        assertEquals("1s² 2s² 2p⁶", formatConfiguration("1s2 2s2 2p6"))
    }

    @Test
    fun testFormatLabel() {
        assertEquals("Kα₁", formatLabel("Ka1"))
        assertEquals("Kβ₁", formatLabel("Kb1"))
        assertEquals("2p₃⁄₂", formatLabel("2p3/2"))
        assertEquals("CH₃Cl", formatLabel("CH3Cl"))
        assertEquals("SiO₂", formatLabel("SiO2"))
    }
}
