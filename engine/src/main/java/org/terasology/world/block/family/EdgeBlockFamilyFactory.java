/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.world.block.family;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.Edge;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockBuilderHelper;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.loader.BlockFamilyDefinition;
import org.terasology.world.block.shapes.BlockShape;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Set;


@RegisterBlockFamilyFactory("edge")
public class EdgeBlockFamilyFactory implements BlockFamilyFactory {
    private static Logger logger = LoggerFactory.getLogger(EdgeBlockFamilyFactory.class);

    @Override
    public BlockFamily createBlockFamily(BlockFamilyDefinition definition, BlockBuilderHelper blockBuilder) {
        throw new IllegalStateException("A shape must be provided when creating a family for a edge block family definition");
    }

    @Override
    public BlockFamily createBlockFamily(BlockFamilyDefinition definition, BlockShape shape, BlockBuilderHelper blockBuilder) {
        if (!definition.isFreeform()) {
            throw new IllegalStateException("A shape cannot be provided when creating a family for a non-freeform block family definition");
        }
        BlockUri uri = new BlockUri(definition.getUrn(), shape.getUrn());

        Edge archetypeEdge = Edge.of(shape.getBlockShapePlacement().getArchetype());

        boolean symmetric = shape.getBlockShapePlacement().getSymmetry().hasEdgeSymmetry();

        EnumMap<Edge, Block> blockMap = new EnumMap<Edge, Block>(Edge.class);
        for( Edge edge : Edge.symmetricSubset() ) {
            Edge equivalentEdge = edge.getSymmetricEquivalent();
            Block block, equivalentBlock;
            block = blockBuilder.constructTransformedBlock(definition, shape, edge.toString(), edge.getRotationFromBottomBack());
            block.setUri(new BlockUri(uri, edge.getName()));
            if( symmetric ) {
                equivalentBlock = block;
            } else {
                equivalentBlock = blockBuilder.constructTransformedBlock(definition, shape, equivalentEdge.toString(), equivalentEdge.getRotationFromBottomBack());
                equivalentBlock.setUri(new BlockUri(uri, equivalentEdge.getName()));
            }
            blockMap.put(edge, block);
            blockMap.put(equivalentEdge, equivalentBlock);
        }

        return new EdgeBlockFamily(uri, archetypeEdge, blockMap, definition.getCategories());
    }

    @Override
    public Set<String> getSectionNames() {
        return Collections.emptySet();
    }

    @Override
    public ImmutableList<MultiSection> getMultiSections() {
        return ImmutableList.of();
    }

    @Override
    public boolean isFreeformSupported() {
        return false;
    }
}
