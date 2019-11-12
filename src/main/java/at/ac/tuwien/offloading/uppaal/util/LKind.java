package at.ac.tuwien.offloading.uppaal.util;

/**
 * Valid kinds of labels on locations.
 */
public enum LKind {
    name,
    init,
    urgent,
    committed,
    invariant,
    exponentialrate,
    comments
};