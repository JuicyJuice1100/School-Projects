//////////////////////////  Arena class /////////////////////////////////

function Arena() {

	this.vertices = [
		//East Wall
		ARENASIZE*.25,0.0,ARENASIZE,
        ARENASIZE*.25,0.0,-ARENASIZE*2,
        ARENASIZE*.25,WALLHEIGHT,-ARENASIZE*2,
		ARENASIZE*.25,WALLHEIGHT/2,ARENASIZE,
		
		//West Wall
		ARENASIZE*.75,0.0,ARENASIZE,
        ARENASIZE*.75,WALLHEIGHT/2, ARENASIZE,
        ARENASIZE*.75,WALLHEIGHT,-ARENASIZE*2,
		ARENASIZE*.75,0.0,-ARENASIZE*2,
		
		//North Wall
		ARENASIZE*.6,0.0,-ARENASIZE/2,
        ARENASIZE*.4,0.0,-ARENASIZE/2,
        ARENASIZE*.6,1.0,-ARENASIZE/2,
		ARENASIZE*.4,1.0,-ARENASIZE/2,
		
		//South Wall
		ARENASIZE*.25,0.0,0.0,
        ARENASIZE*.25,WALLHEIGHT,0.0,
        ARENASIZE*.75,WALLHEIGHT,0.0,
		ARENASIZE*.75,0.0,0.0,
		
		//Floor
		0.0, 0.0, ARENASIZE,
		ARENASIZE, 0.0, ARENASIZE,
		ARENASIZE, 0.0, -ARENASIZE*2,
		0.0, 0.0, -ARENASIZE*2
	];

	this.normals = [
		1.0, 0.0, 0.0,
		1.0, 0.0, 0.0,
		1.0, 0.0, 0.0,
		1.0, 0.0, 0.0,
		-1.0, 0.0, 0.0,
		-1.0, 0.0, 0.0,
		-1.0, 0.0, 0.0,
		-1.0, 0.0, 0.0,
		0.0, 0.0, 1.0,
		0.0, 0.0, 1.0,
		0.0, 0.0, 1.0,
		0.0, 0.0, 1.0,
		0.0, 0.0, -1.0,
		0.0, 0.0, -1.0,
		0.0, 0.0, -1.0,
		0.0, 0.0, -1.0,
		0.0, 1.0, 0.0,
		0.0, 1.0, 0.0,
		0.0, 1.0, 0.0,
		0.0, 1.0, 0.0
	];

	this.vBuffer = null;
	this.nBuffer = null;
	this.vPosition = null;
	this.vNormal = null;

	this.init = function () {

		this.vBuffer = gl.createBuffer();
		gl.bindBuffer(gl.ARRAY_BUFFER, this.vBuffer);
		gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(this.vertices), gl.STATIC_DRAW);

		this.nBuffer = gl.createBuffer();
		gl.bindBuffer(gl.ARRAY_BUFFER, this.nBuffer);
		gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(this.normals), gl.STATIC_DRAW);

	};

	this.show = function () {

		gl.enable(gl.DEPTH_TEST);

		gl.bindBuffer(gl.ARRAY_BUFFER, this.vBuffer);
		this.vPosition = gl.getAttribLocation(program, "vPosition");
		if (this.vPosition < 0) {
			console.log('Failed to get the storage location of vPosition');
		}
		gl.vertexAttribPointer(this.vPosition, 3, gl.FLOAT, false, 0, 0);
		gl.enableVertexAttribArray(this.vPosition);

		gl.bindBuffer(gl.ARRAY_BUFFER, this.nBuffer);
		this.vNormal = gl.getAttribLocation(program, "vNormal");
		if (this.vPosition < 0) {
			console.log('Failed to get the storage location of vPosition');
		}
		gl.vertexAttribPointer(this.vNormal, 3, gl.FLOAT, false, 0, 0);
		gl.enableVertexAttribArray(this.vNormal);

		var ambientProduct = mult(la0, ma);
		var diffuseProduct = mult(ld0, md);
		var specularProduct = mult(ls0, ms);

		gl.uniform4fv(gl.getUniformLocation(program, "ambientProduct"),
			flatten(ambientProduct));
		gl.uniform4fv(gl.getUniformLocation(program, "diffuseProduct"),
			flatten(diffuseProduct));
		gl.uniform4fv(gl.getUniformLocation(program, "specularProduct"),
			flatten(specularProduct));
		gl.uniform4fv(gl.getUniformLocation(program, "lightPosition"),
			flatten(lp0));
		gl.uniform1f(gl.getUniformLocation(program, "shininess"),
			me);

		gl.drawArrays(gl.TRIANGLE_FAN, 0, 4);
		gl.drawArrays(gl.TRIANGLE_FAN, 4, 4);
		gl.drawArrays(gl.TRIANGLE_FAN, 8, 4);
		// gl.drawArrays(gl.TRIANGLE_FAN, 12, 4);

		ambientProduct = mult(la0, blue);
		// ambientProduct = mult(vec4(1.0,1.0,1.0,1.0), blue);
		diffuseProduct = mult(ld0, blue);
		specularProduct = mult(ls0, blue);

		gl.uniform4fv(gl.getUniformLocation(program, "ambientProduct"),
			flatten(ambientProduct));
		gl.uniform4fv(gl.getUniformLocation(program, "diffuseProduct"),
			flatten(diffuseProduct));
		gl.uniform4fv(gl.getUniformLocation(program, "specularProduct"),
			flatten(specularProduct));

		gl.drawArrays(gl.TRIANGLE_FAN, 16, 4)
		// IMPORTANT: Disable current vertex attribute arrays so those in
		// a different object can be activated.  
		gl.disableVertexAttribArray(this.vPosition);
		gl.disableVertexAttribArray(this.vNormal);
	};

};

//////////////////////////  End Arena object /////////////////////////////////
