# How to Release a New GEF Version

1. Determine build number of [gef-master](https://ci.eclipse.org/gef/job/gef-master/) that is to be promoted as a release.

1. Go to [Releng/Release-Promotion](https://ci.eclipse.org/gef/job/Releng/job/Release-Promotion/) and select `Build with Parameters`. Enter values as specified below and click `Build`.

    * PROMOTED_BUILD: Enter specific build number (recommended) or use a Jenkins permalink specifier (such as lastStableBuild).
    * RELEASE_TYPE: Select (I)ntegration, (S)table (=Milestones) or (R)elease.
    * PROMOTE_TO_UPDATE_SITE: Select `y` unless you *recently* read the releng scripts and know what this means.
    * MERGE_WITH_EXISTING_UPDATE_SITE: Select `y` unless you *recently* read the releng scripts and know what this means.
    * PROMOTE_TO_DROP_LOCATION: Select `y` unless you *recently* read the releng scripts and know what this means.
    * RELEASE_LABEL: Enter version that is to be released, e.g. `5.3.1`.
    * RELEASE_LABEL_SUFFIX: Optional suffix, enter M[1-3] for milestones, leave empty for GA releases.

1. When the promotion build finished, go to the *promoted* build (the one you chose in step 1) and mark the build as "Keep forever". Also, add a description to this build, specifying which version it just became, for example: `5.3.1.M3 (2020-09)`.

1. The promotion build will have created new update sites, which have to be added to the SimRel repository. Make sure you understand the [SimRel](https://wiki.eclipse.org/Simrel/Contributing_to_Simrel_Aggregation_Build). The SimRel repository contains a file called `gef.aggrcon` (XML) which needs to be edited.

    * The file contains multiple `repositories` elements, but only one pointing to the [GEF Update Site](https://download.eclipse.org/tools/gef/updates/) or more accurately, a specific sub site. Adapt this element by pointing its `location` attribute to the newly created update site. Also, make sure the `description` attribute matches the new site (GEF Milestones vs GEF Releases).
    * The just mentioned `repositories` element contains a series of `features` elements. For each of those features, look up its current version from the new update site and update the `versionRange` attribute accordingly. **Note:** Every feature has its own version, even if the timestamps are the same.
    * If in doubt, check the file's history for older commits.
    * Commit those changes with a speaking message, e.g. `GEF 5.3.1.M3 Contribution for 2020-09`.
    * Leave the Change-ID as-is and push your commit to Gerrit. EGit will give you the link to your Gerrit review in the success pop-up. Visit that link, wait for the CI to test this contribution and review/submit this change.

1. If this was a GA release, draft a new release on [GitHub](https://github.com/eclipse/gef).

1. In case of a GA release, adapt the default values in the promote.Jenkinsifle to use the new versions. When the new values are on the master branch, run a single Release-Promotion for an integration build, applying those values manually. After that, new Release-Promotion runs should use the new default values.
