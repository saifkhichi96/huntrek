function createSphereVertices(radius, color, latitudeBands = 30, longitudeBands = 30) {
    var sphere = new Object();
    sphere.indexData = [];
    sphere.vertexPositionData = [];
    sphere.vertexColorData = [];
    sphere.normalData = [];
    for (var latNumber = 0; latNumber <= latitudeBands; latNumber++) {
        var theta = latNumber * Math.PI / latitudeBands;
        var sinTheta = Math.sin(theta);
        var cosTheta = Math.cos(theta);
        for (var longNumber = 0; longNumber <= longitudeBands; longNumber++) {
            var phi = longNumber * 2 * Math.PI / longitudeBands;
            var sinPhi = Math.sin(phi);
            var cosPhi = Math.cos(phi);
            var x = cosPhi * sinTheta;
            var y = cosTheta;
            var z = sinPhi * sinTheta;
            var u = 1 - (longNumber / longitudeBands);
            var v = 1 - (latNumber / latitudeBands);

            sphere.normalData.push(x);
            sphere.normalData.push(y);
            sphere.normalData.push(z);

            sphere.vertexColorData.push(color[0]);
            sphere.vertexColorData.push(color[1]);
            sphere.vertexColorData.push(color[2]);

            sphere.vertexPositionData.push(radius * x);
            sphere.vertexPositionData.push(radius * y);
            sphere.vertexPositionData.push(radius * z);

            if (latNumber < latitudeBands && longNumber < longitudeBands) {
                var first = (latNumber * (longitudeBands + 1)) + longNumber;
                var second = first + longitudeBands + 1;
                sphere.indexData.push(first);
                sphere.indexData.push(second);
                sphere.indexData.push(first + 1);

                sphere.indexData.push(second);
                sphere.indexData.push(second + 1);
                sphere.indexData.push(first + 1);
            }
        }
    }
    return sphere;
}

function Sphere(gl, radius, color = [1.0, 1.0, 1.0], density = [30, 30]) {
    this.color = color;
    this.position = [1.0, 1.0, 1.0];
    this.radius = radius;
    this.scaleFactor = 1.0;
    this.revolve = 0;
    this.pivot = this.position;
    this.rotation = 0;

    var vertices = createSphereVertices(this.radius, color, density[0], density[1]);

    this.vertexNormalBuffer = gl.createBuffer();
    this.vertexNormalBuffer.itemSize = 3;
    this.vertexNormalBuffer.numItems = vertices.normalData.length / 3;
    gl.bindBuffer(gl.ARRAY_BUFFER, this.vertexNormalBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(vertices.normalData), gl.STATIC_DRAW);

    this.vertexPositionBuffer = gl.createBuffer();
    this.vertexPositionBuffer.itemSize = 3;
    this.vertexPositionBuffer.numItems = vertices.vertexPositionData.length / 3;
    gl.bindBuffer(gl.ARRAY_BUFFER, this.vertexPositionBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(vertices.vertexPositionData), gl.STATIC_DRAW);

    this.vertexColorBuffer = gl.createBuffer();
    this.vertexColorBuffer.itemSize = 3;
    this.vertexColorBuffer.numItems = vertices.vertexColorData.length / 3;
    gl.bindBuffer(gl.ARRAY_BUFFER, this.vertexColorBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(vertices.vertexColorData), gl.STATIC_DRAW);

    this.vertexIndexBuffer = gl.createBuffer();
    this.vertexIndexBuffer.numItems = vertices.indexData.length;
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.vertexIndexBuffer);
    gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(vertices.indexData), gl.STATIC_DRAW);
}

Sphere.prototype.setPosition = function (position) {
    this.position = position;
}

Sphere.prototype.scale = function (S) {
    this.scaleFactor = S;
}

Sphere.prototype.rotateAround = function (rotation, pivot) {
    this.revolve += rotation;
    this.pivot = pivot;
}

Sphere.prototype.render = function (gl, shaderProgram, mvMatrix, viewMatrix) {
    mat4.identity(mvMatrix);
    mat4.multiply(modelMatrix, viewMatrix);

    mat4.translate(mvMatrix, this.pivot);
    mat4.rotate(mvMatrix, degToRad(this.revolve), [0, 1, 0]);
    mat4.translate(mvMatrix, [-this.pivot[0], -this.pivot[1], -this.pivot[2]]);
    mat4.translate(mvMatrix, this.position);
    mat4.scale(mvMatrix, [this.scaleFactor, this.scaleFactor, this.scaleFactor]);

    gl.bindBuffer(gl.ARRAY_BUFFER, this.vertexPositionBuffer);
    gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, this.vertexPositionBuffer.itemSize, gl.FLOAT, false, 0, 0);

    gl.bindBuffer(gl.ARRAY_BUFFER, this.vertexColorBuffer);
    gl.vertexAttribPointer(shaderProgram.vertexColorAttribute, this.vertexColorBuffer.itemSize, gl.FLOAT, false, 0, 0);

    gl.bindBuffer(gl.ARRAY_BUFFER, this.vertexNormalBuffer);
    gl.vertexAttribPointer(shaderProgram.vertexNormalAttribute, this.vertexNormalBuffer.itemSize, gl.FLOAT, false, 0, 0);

    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.vertexIndexBuffer);
    setMatrixUniforms(gl);

    gl.drawElements(gl.TRIANGLES, this.vertexIndexBuffer.numItems, gl.UNSIGNED_SHORT, 0);
}