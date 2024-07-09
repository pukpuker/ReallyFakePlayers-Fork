/*
 * Copyright 2023 Marlester
 *
 * Licensed under the EUPL, Version 1.2 (the "License");
 *
 * You may not use this work except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

package me.marlester.rfp.faketools;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import me.marlester.rfp.config.ConfigYml;
import me.marlester.rfp.config.NameListYml;
import org.bukkit.BanList;
import org.bukkit.Bukkit;

/**
 * A singleton class responsible for generating fake names. This class relies on {@link FakeLister}
 * to ensure uniqueness of names among fake players. It also uses {@link NameListYml} to retrieve
 * and store a list of names.
 */
@Singleton
public class FakeNamer {
  private final FakeLister fakeLister;

  @Getter
  private final ImmutableList<String> names;

  @Inject
  FakeNamer(FakeLister fakeLister, NameListYml nameListYml, ConfigYml configYml) {
    this.fakeLister = fakeLister;
    var configMain = configYml.getConfiguration();
    var configNameYml = nameListYml.getConfiguration();
    names = ImmutableList.copyOf(configNameYml.getStringList("names"));
    int maxFakePlayers = configMain.getInt("max-fake-players");
    if (maxFakePlayers > names.size()) {
      throw new IllegalStateException("Number of names in name-list.yml must not be less "
          + "than max-fake-players in config.yml!");
    }
  }

  /**
   * Retrieves a random, unique name from the list of names. This method ensures that the returned
   * name is not already used by any existing fake player.
   *
   * @return a unique random name as a {@link String}.
   */
  public String getRandomName(boolean can_ban) {
    var rawFakePlayersByName = fakeLister.getRawFakePlayersByName();
    List<String> availableNames = new ArrayList<>(names);
    String name;

    do {
      if (availableNames.isEmpty()) {
        return "ALL_ACCOUNTS_HAS_BEEN_BANNED";
      }
      int index = ThreadLocalRandom.current().nextInt(availableNames.size());
      name = availableNames.get(index);
      availableNames.remove(index);

    } while (rawFakePlayersByName.containsKey(name) || (can_ban && Bukkit.getBanList(BanList.Type.NAME).isBanned(name)));

    return name;
  }
}
