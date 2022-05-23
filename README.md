# RedisEconomyBridge
Sync player's Vault economy balance across Spigot servers using Redis!

## Command
- `/reco check <player>` Check player's both local balance and balance synced on Redis
- `/reco set <player> <amount>` Set player's  balance (local and synced)
- `/reco give|take <player> <amount>` Give or take from player's balance (local and synced)

## Permission
- `reco.admin` Access to /reco command

## Config file
- `host`, `port`, `timeout`, `password` Your Redis information
- `namespace` Prefix for Redis key
- `interval` Time in seconds to sync all online player's balance
- `hook` Since Vault doesn't have balance change event you can hook to some economy plugins to sync a player's balance when it's changed