// SPDX-FileCopyrightText: 2021 RTE FRANCE
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.sct.commons.dto;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.sct.commons.scl.com.ConnectedAPAdapter;
import org.lfenergy.compas.sct.commons.scl.com.SubNetworkAdapter;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubNetworkDTOTest {

    @Test
    void testConstructor(){
        SubNetworkDTO subNetworkDTO = new SubNetworkDTO();
        assertTrue(subNetworkDTO.getConnectedAPs().isEmpty());

        subNetworkDTO = new SubNetworkDTO("sName","IP");
        assertEquals("sName",subNetworkDTO.getName());
        assertEquals("IP",subNetworkDTO.getType());
    }

    @Test
    void testFrom(){
        SubNetworkAdapter subNetworkAdapter = Mockito.mock(SubNetworkAdapter.class);
        ConnectedAPAdapter connectedAPAdapter = Mockito.mock(ConnectedAPAdapter.class);
        Mockito.when(subNetworkAdapter.getConnectedAPAdapters()).thenReturn(List.of(connectedAPAdapter));
        Mockito.when(subNetworkAdapter.getName()).thenReturn("sName");
        Mockito.when(subNetworkAdapter.getType()).thenReturn(SubNetworkDTO.SubnetworkType.IP.toString());
        Mockito.when(connectedAPAdapter.getApName()).thenReturn(DTO.AP_NAME);
        Mockito.when(connectedAPAdapter.getIedName()).thenReturn(DTO.HOLDER_IED_NAME);

        SubNetworkDTO subNetworkDTO = SubNetworkDTO.from(subNetworkAdapter);
        assertEquals("sName",subNetworkDTO.getName());
        assertEquals("IP",subNetworkDTO.getType());
        assertFalse(subNetworkDTO.getConnectedAPs().isEmpty());

    }

}