/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.loader.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.forgespi.language.IModInfo;

/**
 * Represents a mod.
 */
public interface ModContainer {
    FMLModContainer fmlcontainer();
    /**
     * Returns the metadata of this mod.
     */
    IModInfo getMetadata();

    /**
     * Returns the root directories of the mod (inside JAR/folder), exposing its contents.
     *
     * <p>The paths may point to regular folders or into mod JARs. Multiple root paths may occur in development
     * environments with {@code -Dfabric.classPathGroups} as used in multi-project mod setups.
     *
     * <p>A path returned by this method may be incompatible with {@link Path#toFile} as its FileSystem doesn't
     * necessarily represent the OS file system, but potentially a virtual view of jar contents or another abstraction.
     *
     * @return the root directories of the mod, may be empty for builtin or other synthetic mods
     */
    List<Path> getRootPaths();

    /**
     * Gets an NIO reference to a file inside the JAR/folder.
     *
     * <p>The path, if present, is guaranteed to exist!
     *
     * <p>A path returned by this method may be incompatible with {@link Path#toFile} as its FileSystem doesn't
     * necessarily represent the OS file system, but potentially a virtual view of jar contents or another abstraction.
     *
     * @param file The location from a root path, using {@code /} as a separator.
     * @return optional containing the path to a given file or empty if it can't be found
     */
    default Optional<Path> findPath(String file) {
        for (Path root : getRootPaths()) {
            Path path = root.resolve(file.replace("/", root.getFileSystem().getSeparator()));
            if (Files.exists(path)) return Optional.of(path);
        }

        return Optional.empty();
    }

    /**
     * Get the mod containing this mod (nested jar parent).
     *
     * @return mod containing this mod or empty if not nested
     */
    Optional<ModContainer> getContainingMod();

    /**
     * Get the active mods contained within this mod (nested jar children).
     *
     * @return active contained mods within this mod's jar
     */
    Collection<ModContainer> getContainedMods();

    // deprecated methods

    /**
     * @deprecated use {@link #getRootPaths()} instead
     */
    @Deprecated
    default Path getRoot() {
        return getRootPath();
    }

    /**
     * @deprecated use {@link #getRootPaths()} instead
     */
    @Deprecated
    Path getRootPath();

    /**
     * @deprecated use {@link #findPath} instead
     */
    @Deprecated
    Path getPath(String file);
}