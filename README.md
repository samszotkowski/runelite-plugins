# <img align="center" src="https://github.com/samszotkowski/runelite-plugins/blob/slayer-history/icon.png" /> Slayer History
<img align="right" alt="plugin panel demo" src="https://github.com/samszotkowski/runelite-plugins/blob/slayer-history/readme-img0.png" />

A simple plugin to record completed Slayer tasks. The tasks will appear in the sidebar under the enchanted gem icon, as well as in a log file found at:

``.runelite/slayer-history/{account hash}/tasks.log``

Each combination of account and game mode (e.g., Leagues, Deadman) will have a different account hash folder. If you wish to clear your history, simply delete the `tasks.log` file within.

In the settings there is an option to change the date format, and a toggle to show/hide Slayer tasks that were skipped.

## Changelog
v1.3 - Show active task at the top of the panel. Fix bugs with Turael skipping. Update several task icons to look cooler. Mortimer tasks should now be tracked properly.

v1.2 - Streak # after completing each task (or wildy streak # after completing a Krystilia task) now shows up as a number in the bottom right. Previous tasks won't have any data, but if you want numbers to display for old entries you can edit the numbers in yourself.

To do this, edit the lines in `tasks.log` so they look like `{...,"taskStreak":1000}` then save the file, and turn the plugin off and on to refresh it.

Also fixed a bug so cancelling/blocking/storing tasks should all now properly display as skips.

1.1 - Skipped tasks now show up as a bank filler icon and the name is red. There is a toggle in the settings to show/hide skipped tasks.
