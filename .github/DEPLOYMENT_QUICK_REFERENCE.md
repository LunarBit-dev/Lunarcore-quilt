# Quick Deployment Reference

## Automatic Deployment Rules

```
Version ends with -SNAPSHOT → maven-snapshots (any branch)
Version without -SNAPSHOT → maven-releases (main branch only)
```

## Set Version

Edit `gradle.properties`:
```properties
version = 1.0.0          # Release
version = 1.0.0-SNAPSHOT # Snapshot
```

## GitHub Secrets Required

```
NEXUS_USER     = deploy-user
NEXUS_PASSWORD = your-password
```

**Add at:** Settings → Secrets and variables → Actions → New repository secret

## Deploy Process

```bash
# 1. Set version in gradle.properties
# 2. Commit changes
git add gradle.properties
git commit -m "Version 1.0.0"

# 3. Push (triggers deployment)
git push
```

## Local Testing

```bash
# With credentials
export NEXUS_USER=your-user
export NEXUS_PASSWORD=your-pass
./gradlew publish

# Without deployment (build only)
./gradlew build
```

## Usage in Other Projects

```gradle
repositories {
    maven { url 'https://maven.lunarbit.dev/repository/maven-public' }
}

dependencies {
    modImplementation 'dev.lunarbit.lunarcore:lunarcore-quilt:1.0.0+1.20.6'
}
```

## Workflow Triggers

- **Push to main**: Deploy releases and snapshots
- **Push to any branch**: Deploy snapshots only
- **Release protection**: Non-snapshot versions require main branch

## Check Deployment

1. **GitHub Actions**: Repository → Actions tab
2. **Nexus Browser**: https://maven.lunarbit.dev
3. **Artifacts**: Download from workflow run

## Common Issues

| Issue | Solution |
|-------|----------|
| 401 Unauthorized | Check GitHub Secrets |
| Release only from main | Push to main or use -SNAPSHOT |
| Build fails | Check Java 21 and dependencies |
| Repository not found | Verify Nexus repository names |

## Workflow File

`.github/workflows/deploy.yml` - Runs automatically, no manual steps needed.

## Full Documentation

See `DEPLOYMENT.md` for complete setup guide.

---

**Zero configuration required after initial setup!**

