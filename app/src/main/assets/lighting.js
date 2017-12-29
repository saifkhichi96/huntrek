function DirectionalLight(directionUniform, colorUniform) {
    this.directionUniform = directionUniform;
    this.direction = [0.0, 0.0, 0.0];

    this.colorUniform = colorUniform;
    this.color = [1.0, 1.0, 1.0];
}

DirectionalLight.prototype.setDirection = function (x, y, z) {
    var lightDirection = [x, y, z];
    vec3.normalize(lightDirection, vec3.create());
    vec3.scale(lightDirection, -1);

    this.direction = lightDirection;
}

DirectionalLight.prototype.setColor = function (r, g, b) {
    this.color = [r, g, b];
}

DirectionalLight.prototype.render = function (gl) {
    gl.uniform3fv(this.directionUniform, this.direction);
    gl.uniform3fv(this.colorUniform, this.color);
}